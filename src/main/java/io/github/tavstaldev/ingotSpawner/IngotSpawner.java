package io.github.tavstaldev.ingotSpawner;

import io.github.tavstaldev.ingotSpawner.commands.CommandIngot;
import io.github.tavstaldev.ingotSpawner.events.PlayerEventListener;
import io.github.tavstaldev.ingotSpawner.task.SpawnTask;
import io.github.tavstaldev.ingotSpawner.util.EconomyUtils;
import io.github.tavstaldev.minecorelib.PluginBase;
import io.github.tavstaldev.minecorelib.core.PluginLogger;
import io.github.tavstaldev.minecorelib.core.PluginTranslator;
import org.bukkit.Bukkit;

/**
 * Main class for the IngotSpawner plugin.
 * <p>
 * This class extends `PluginBase` and serves as the entry point for the plugin.
 * It handles initialization, configuration loading, event registration, and task scheduling.
 * </p>
 */
public final class IngotSpawner extends PluginBase {
    /**
     * Singleton instance of the plugin.
     */
    public static IngotSpawner Instance;

    /**
     * Manages the storage and retrieval of ingot spawn locations.
     */
    private IngotLocations locations;

    /**
     * Task responsible for spawning ingots at predefined locations.
     */
    private SpawnTask spawnTask;

    /**
     * Retrieves the plugin's custom logger.
     *
     * @return The `PluginLogger` instance.
     */
    public static PluginLogger logger() {
        return Instance.getCustomLogger();
    }

    /**
     * Retrieves the plugin's translator for handling localizations.
     *
     * @return The `PluginTranslator` instance.
     */
    public static PluginTranslator translator() {
        return Instance.getTranslator();
    }

    /**
     * Retrieves the plugin's configuration.
     *
     * @return The `IngotSpawnerConfig` instance.
     */
    public static IngotSpawnerConfig config() {
        return (IngotSpawnerConfig) Instance._config;
    }

    /**
     * Retrieves the ingot spawn locations manager.
     *
     * @return The `IngotLocations` instance.
     */
    public static IngotLocations getLocations() {
        return Instance.locations;
    }

    /**
     * Constructor for the `IngotSpawner` class.
     * Initializes the plugin with the update URL.
     */
    public IngotSpawner() {
        super(false, "https://github.com/TavstalDev/IngotSpawner/releases/latest");
    }

    /**
     * Called when the plugin is enabled.
     * <p>
     * This method initializes the plugin, loads configurations, registers commands,
     * event listeners, and tasks, and checks for updates.
     * </p>
     */
    @Override
    public void onEnable() {
        Instance = this;
        _config = new IngotSpawnerConfig();
        _config.load();
        _translator = new PluginTranslator(this, new String[]{"eng", "hun"});
        locations = new IngotLocations();
        locations.load();
        _logger.info(String.format("Loading %s...", getProjectName()));

        // Load localizations.
        if (!_translator.load()) {
            _logger.error("Failed to load localizations... Unloading...");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        // Register Economy
        _logger.debug("Hooking into Vault...");
        if (EconomyUtils.setupEconomy())
            _logger.info("Economy plugin found and hooked into Vault.");
        else
        {
            _logger.warn("Economy plugin not found. Unloading...");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        // Register commands.
        _logger.debug("Registering commands...");
        new CommandIngot();

        // Register event listeners.
        new PlayerEventListener().register();

        // Register tasks
        if (spawnTask != null && !spawnTask.isCancelled()) {
            spawnTask.cancel();
        }
        spawnTask = new SpawnTask();
        spawnTask.runTaskTimer(this, 0L, 20 * 60 * 5);

        _logger.ok(String.format("%s has been successfully loaded.", getProjectName()));

        // Check for plugin updates if enabled in the configuration.
        if (getConfig().getBoolean("checkForUpdates", true)) {
            isUpToDate().thenAccept(upToDate -> {
                if (upToDate) {
                    _logger.ok("Plugin is up to date!");
                } else {
                    _logger.warn("A new version of the plugin is available: " + getDownloadUrl());
                }
            }).exceptionally(e -> {
                _logger.error("Failed to determine update status: " + e.getMessage());
                return null;
            });
        }
    }

    /**
     * Called when the plugin is disabled.
     * <p>
     * This method performs cleanup tasks and logs the plugin's unloading.
     * </p>
     */
    @Override
    public void onDisable() {
        _logger.info(String.format("%s has been successfully unloaded.", getProjectName()));
    }

    /**
     * Reloads the plugin's configuration, localizations, and tasks.
     * <p>
     * This method cancels the current spawn task, reloads the configuration and localizations,
     * and schedules a new spawn task.
     * </p>
     */
    public void reload() {
        _logger.info(String.format("Reloading %s...", getProjectName()));
        _logger.debug("Reloading localizations...");
        _translator.load();
        _logger.debug("Localizations reloaded.");
        _logger.debug("Reloading configuration...");
        this._config.load();
        _logger.debug("Configuration reloaded.");

        if (spawnTask != null && !spawnTask.isCancelled()) {
            spawnTask.cancel();
        }
        spawnTask = new SpawnTask();
        spawnTask.runTaskTimer(this, 0L, 6000L);

        _logger.ok(String.format("%s has been successfully reloaded.", getProjectName()));
    }
}
