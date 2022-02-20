package dev.ratas.aggressiveanimals.config;

public class Settings {
    private final CustomConfigHandler config;

    public Settings(CustomConfigHandler config) {
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

}
