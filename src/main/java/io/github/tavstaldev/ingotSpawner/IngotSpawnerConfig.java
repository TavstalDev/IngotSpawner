package io.github.tavstaldev.ingotSpawner;

import io.github.tavstaldev.ingotSpawner.model.Ingot;
import io.github.tavstaldev.minecorelib.config.ConfigurationBase;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Configuration class for the IngotSpawner plugin.
 * <p>
 * This class extends `ConfigurationBase` to manage the `config.yml` file,
 * which contains general plugin settings and ingot definitions.
 * It provides methods to load default values, retrieve ingots by material,
 * and select random ingots based on their weights.
 * </p>
 */
public class IngotSpawnerConfig extends ConfigurationBase {

    /**
     * Constructor for the `IngotSpawnerConfig` class.
     * Initializes the configuration file `config.yml`.
     */
    public IngotSpawnerConfig() {
        super(IngotSpawner.Instance, "config.yml", null);
    }

    // General plugin settings
    public String locale, prefix;
    public boolean usePlayerLocale, checkForUpdates, debug;

    // Set of custom ingots defined in the configuration
    public Set<Ingot> ingots;

    /**
     * Loads default values for the configuration if none exist.
     * <p>
     * This method initializes general plugin settings and ingot definitions.
     * If no ingots are defined, it creates default entries for gold, diamond, and emerald ingots.
     * </p>
     */
    @Override
    protected void loadDefaults() {
        // General settings
        locale = resolveGet("locale", "eng");
        usePlayerLocale = resolveGet("usePlayerLocale", true);
        checkForUpdates = resolveGet("checkForUpdates", true);
        debug = resolveGet("debug", false);
        prefix = resolveGet("prefix", "&bIngots&3Spawner &8»");

        // Initialize ingots
        ingots = new LinkedHashSet<>();
        if (this.get("ingots") == null) {
            resolve("ingots", new LinkedHashMap<String, Object>(Map.of(
                    "gold", Map.of("material", "GOLD_INGOT", "weight", 70, "moneyReward", 25),
                    "diamond", Map.of("material", "DIAMOND", "weight", 20, "moneyReward", 50),
                    "emerald", Map.of("material", "EMERALD", "weight", 10, "moneyReward", 100)
            )));
        }

        // Load ingot definitions from the configuration
        var ingotConfigs = this.getConfigurationSection("ingots");
        if (ingotConfigs == null) {
            return;
        }
        for (var entry : ingotConfigs.getKeys(false)) {
            var ingotData = ingotConfigs.getConfigurationSection(entry);
            if (ingotData == null)
                continue;
            var materialName = ingotData.getString("material", "GOLD_INGOT");
            var material = Material.getMaterial(materialName);
            if (material == null) {
                IngotSpawner.logger().error("Invalid material name: " + materialName);
                continue;
            }
            var weight = ingotData.getInt("weight", 10);
            var moneyReward = ingotData.getDouble("moneyReward", 25);
            ingots.add(new Ingot(material, weight, moneyReward));
        }
    }

    /**
     * Retrieves a custom ingot by its material type.
     *
     * @param material The `Material` to search for.
     * @return The corresponding `Ingot` object, or `null` if not found.
     */
    public @Nullable Ingot getIngotByMaterial(Material material) {
        for (Ingot ingot : ingots) {
            if (ingot.material() == material) {
                return ingot;
            }
        }
        return null;
    }

    /**
     * Selects a random ingot based on their weights.
     * <p>
     * The weights of the ingots are used to determine the probability of selection.
     * </p>
     *
     * @return A randomly selected `Ingot` object.
     */
    public Ingot getRandomIngot() {
        int totalWeight = ingots.stream().mapToInt(Ingot::weight).sum();
        int randomWeight = new Random().nextInt(totalWeight);
        int currentWeight = 0;
        for (Ingot ingot : ingots) {
            currentWeight += ingot.weight();
            if (randomWeight < currentWeight) {
                return ingot;
            }
        }
        return null; // This should never happen
    }
}