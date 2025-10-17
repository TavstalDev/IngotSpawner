package io.github.tavstaldev.ingotSpawner;

import io.github.tavstaldev.ingotSpawner.commands.CommandIngot;
import io.github.tavstaldev.ingotSpawner.events.PlayerEventListener;
import io.github.tavstaldev.ingotSpawner.task.SpawnTask;
import io.github.tavstaldev.minecorelib.PluginBase;
import io.github.tavstaldev.minecorelib.core.PluginLogger;
import io.github.tavstaldev.minecorelib.core.PluginTranslator;
import org.bukkit.Bukkit;

public final class IngotSpawner extends PluginBase {
    public static IngotSpawner Instance;
    private IngotLocations locations;
    private SpawnTask spawnTask;

    public static PluginLogger logger() {
        return Instance.getCustomLogger();
    }

    public static PluginTranslator translator() {
        return Instance.getTranslator();
    }

    public static IngotSpawnerConfig config() {
        return (IngotSpawnerConfig) Instance._config;
    }

    public static IngotLocations getLocations() {
        return Instance.locations;
    }

    public IngotSpawner() {
        super(false, "https://github.com/TavstalDev/IngotSpawner/releases/latest");
    }

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

    @Override
    public void onDisable() {
        _logger.info(String.format("%s has been successfully unloaded.", getProjectName()));
    }

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
