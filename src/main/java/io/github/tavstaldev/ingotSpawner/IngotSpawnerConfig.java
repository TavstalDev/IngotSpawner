package io.github.tavstaldev.ingotSpawner;

import io.github.tavstaldev.ingotSpawner.model.Ingot;
import io.github.tavstaldev.minecorelib.config.ConfigurationBase;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class IngotSpawnerConfig extends ConfigurationBase {
    public IngotSpawnerConfig() {
        super(IngotSpawner.Instance, "config.yml", null);
    }

    // General
    public String locale, prefix;
    public boolean usePlayerLocale, checkForUpdates, debug;

    // Ingots
    public Set<Ingot> ingots;

    @Override
    protected void loadDefaults() {
        // General
        locale = resolveGet("locale", "eng");
        usePlayerLocale = resolveGet("usePlayerLocale", true);
        checkForUpdates = resolveGet("checkForUpdates", true);
        debug = resolveGet("debug", false);
        prefix = resolveGet("prefix", "&bIngots&3Spawner &8»");

        // Ingots
        ingots = new LinkedHashSet<>();
        if (this.get("ingots") == null) {
            resolve("ingots", new LinkedHashMap<String, Object>(Map.of(
                    "gold", Map.of("material", "GOLD_INGOT", "weight", 70, "moneyReward", 25),
                    "diamond", Map.of("material", "DIAMOND", "weight", 20, "moneyReward", 50),
                    "emerald", Map.of("material", "EMERALD", "weight", 10, "moneyReward", 100)
            )));
        }

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

    public @Nullable Ingot getIngotByMaterial(Material material) {
        for (Ingot ingot : ingots) {
            if (ingot.getMaterial() == material) {
                return ingot;
            }
        }
        return null;
    }

    public Ingot getRandomIngot() {
        int totalWeight = ingots.stream().mapToInt(Ingot::getWeight).sum();
        int randomWeight = new Random().nextInt(totalWeight);
        int currentWeight = 0;
        for (Ingot ingot : ingots) {
            currentWeight += ingot.getWeight();
            if (randomWeight < currentWeight) {
                return ingot;
            }
        }
        return null; // This should never happen
    }
}
