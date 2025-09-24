package io.github.tavstaldev.ingotSpawner;

import io.github.tavstaldev.minecorelib.config.ConfigurationBase;

public class IngotSpawnerConfig extends ConfigurationBase {
    public IngotSpawnerConfig() {
        super(IngotSpawner.Instance, "config.yml", null);
    }

    // General
    public String locale, prefix;
    public boolean usePlayerLocale, checkForUpdates, debug;

    @Override
    protected void loadDefaults() {
        // General
        locale = resolveGet("locale", "hun");
        usePlayerLocale = resolveGet("usePlayerLocale", false);
        checkForUpdates = resolveGet("checkForUpdates", false);
        debug = resolveGet("debug", false);
        prefix = resolveGet("prefix", "&fIngots &8»");
    }
}
