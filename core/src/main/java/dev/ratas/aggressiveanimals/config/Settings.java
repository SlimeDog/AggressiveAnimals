package dev.ratas.aggressiveanimals.config;

import dev.ratas.slimedogcore.api.config.SDCConfiguration;
import dev.ratas.slimedogcore.api.config.SDCCustomConfig;

public class Settings {
    private final SDCCustomConfig config;

    public Settings(SDCCustomConfig config) {
        this.config = config;
    }

    public boolean isOnDebug() {
        return config.getConfig().getBoolean("debug", false);
    }

    public boolean enableMetrics() {
        return config.getConfig().getBoolean("enable-metrics", false);
    }

    public boolean checkForUpdates() {
        return config.getConfig().getBoolean("check-for-updates", false);
    }

    public String getUpdateSource() {
        return config.getConfig().getString("update-source", "Hangar");
    }

    public SDCConfiguration getMobSection() {
        return config.getConfig().getConfigurationSection("mobs");
    }

    public SDCConfiguration getDefaultsSection() {
        return config.getConfig().getConfigurationSection("defaults");
    }

}
