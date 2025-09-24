package io.github.tavstaldev.ingotSpawner;

import io.github.tavstaldev.minecorelib.PluginBase;
import io.github.tavstaldev.minecorelib.core.PluginLogger;
import io.github.tavstaldev.minecorelib.core.PluginTranslator;

public final class IngotSpawner extends PluginBase {

    public static IngotSpawner Instance;

    public static PluginLogger Logger() {
        return Instance.getCustomLogger();
    }

    public static PluginTranslator Translator() {
        return Instance.getTranslator();
    }

    public static IngotSpawnerConfig Config() {
        return (IngotSpawnerConfig) Instance._config;
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
        _logger.Info(String.format("Loading %s...", getProjectName()));

        // Load localizations.
        if (!_translator.Load()) {
            _logger.Error("Failed to load localizations... Unloading...");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        // Register commands.
        _logger.Debug("Registering commands...");
        var command = getCommand("openchat");
        if (command != null) {
            command.setExecutor(new CommandChat());
        }

        _logger.Ok(String.format("%s has been successfully loaded.", getProjectName()));

        // Check for plugin updates if enabled in the configuration.
        if (getConfig().getBoolean("checkForUpdates", true)) {
            isUpToDate().thenAccept(upToDate -> {
                if (upToDate) {
                    _logger.Ok("Plugin is up to date!");
                } else {
                    _logger.Warn("A new version of the plugin is available: " + getDownloadUrl());
                }
            }).exceptionally(e -> {
                _logger.Error("Failed to determine update status: " + e.getMessage());
                return null;
            });
        }
    }

    @Override
    public void onDisable() {
        _logger.Info(String.format("%s has been successfully unloaded.", getProjectName()));
    }

    public void reload() {
        _logger.Info(String.format("Reloading %s...", getProjectName()));
        _logger.Debug("Reloading localizations...");
        _translator.Load();
        _logger.Debug("Localizations reloaded.");
        _logger.Debug("Reloading configuration...");
        this._config.load();
        _logger.Debug("Configuration reloaded.");

        _logger.Ok(String.format("%s has been successfully reloaded.", getProjectName()));
    }
}
