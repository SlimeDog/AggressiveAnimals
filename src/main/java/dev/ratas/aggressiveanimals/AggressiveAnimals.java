package dev.ratas.aggressiveanimals;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import dev.ratas.aggressiveanimals.config.ConfigLoadIssueResolver;
import dev.ratas.aggressiveanimals.config.CustomConfigHandler;
import dev.ratas.aggressiveanimals.config.Settings;
import dev.ratas.aggressiveanimals.config.messaging.Messages;

public class AggressiveAnimals extends JavaPlugin {
    private CustomConfigHandler config;
    private Messages messages;
    private Settings settings;

    private void loadDataFromFile() {
        ConfigLoadIssueResolver issues = ConfigLoadIssueResolver.atLoad();
        try {
            config = new CustomConfigHandler(this, "config.yml");
        } catch (InvalidConfigurationException e) {
            issues.logIssue("INVALID CONFIGURATION", "Invalid configuration - disabling", e);
            disableMe(issues);
            return;
        }
        try {
            messages = new Messages(this);
        } catch (InvalidConfigurationException e) {
            issues.logIssue("INVALID MESSAGES", "Invalid messages - disabling", e);
            disableMe(issues);
            return;
        }
        settings = new Settings(this.config);
    }

    @Override
    public void onEnable() {
        loadDataFromFile();
    }

    @Override
    public FileConfiguration getConfig() {
        return config.getConfig();
    }

    public Messages getMessages() {
        return messages;
    }

    public Settings getSettings() {
        return settings;
    }

    private void disableMe(ConfigLoadIssueResolver issues) {
        getLogger().severe(issues.asString());
        getServer().getPluginManager().disablePlugin(this);
    }

}
