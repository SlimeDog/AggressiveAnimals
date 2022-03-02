package dev.ratas.aggressiveanimals;

import org.bstats.bukkit.Metrics;
import org.bukkit.configuration.InvalidConfigurationException;

import dev.ratas.aggressiveanimals.aggressive.AggressivityManager;
import dev.ratas.aggressiveanimals.commands.AggressiveAnimalsCommand;
import dev.ratas.aggressiveanimals.config.ConfigLoadIssueResolver;
import dev.ratas.aggressiveanimals.config.Settings;
import dev.ratas.aggressiveanimals.config.messaging.Messages;
import dev.ratas.aggressiveanimals.hooks.npc.NPCHookManager;
import dev.ratas.aggressiveanimals.listeners.AggressionListener;
import dev.ratas.aggressiveanimals.listeners.MobRegistrationListener;
import dev.ratas.slimedogcore.api.config.SDCCustomConfig;
import dev.ratas.slimedogcore.api.config.exceptions.ConfigException;
import dev.ratas.slimedogcore.impl.SlimeDogCore;

public class AggressiveAnimals extends SlimeDogCore implements IAggressiveAnimals {
    private static final int BSTATS_ID = 14423;
    private SDCCustomConfig config;
    private Messages messages;
    private Settings settings;
    private NPCHookManager npcHookManager;
    private AggressivityManager aggressivityManager;

    private void loadDataFromFile() {
        ConfigLoadIssueResolver issues = ConfigLoadIssueResolver.atLoad();
        try {
            config = getDefaultConfig();
        } catch (ConfigException e) {
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
    public void pluginEnabled() {
        loadDataFromFile();
        npcHookManager = new NPCHookManager();
        aggressivityManager = new AggressivityManager(this, settings, npcHookManager);
        getPluginManager().registerEvents(new MobRegistrationListener(this, aggressivityManager));
        getPluginManager().registerEvents(new AggressionListener(this, aggressivityManager));
        // bstats
        if (settings.enableMetrics()) {
            new Metrics(this, BSTATS_ID);
        }
        // commands
        getCommand("aggressiveanimals").setExecutor(new AggressiveAnimalsCommand(this, messages));
    }

    public ConfigLoadIssueResolver reload() {
        ConfigLoadIssueResolver issues = ConfigLoadIssueResolver.atReload();
        try {
            config.reloadConfig();
        } catch (ConfigException e) {
            issues.logIssue("INVALID CONFIGURATION", "Invalid configuration - disabling", e);
            disableMe(issues);
            return issues;
        }
        try {
            messages.reloadConfig();
        } catch (ConfigException e) {
            issues.logIssue("INVALID CONFIGURATION", "Invalid configuration - disabling", e);
            disableMe(issues);
            return issues;
        }
        aggressivityManager.reload(settings);
        return issues;
    }

    public Messages getMessages() {
        return messages;
    }

    public Settings getSettings() {
        return settings;
    }

    public NPCHookManager getNPCHookManager() {
        return npcHookManager;
    }

    public AggressivityManager getAggressivityManager() {
        return aggressivityManager;
    }

    private void disableMe(ConfigLoadIssueResolver issues) {
        getLogger().severe(issues.asString());
        getServer().getPluginManager().disablePlugin(this);
    }

    @Override
    public void debug(String msg) {
        if (settings.isOnDebug()) {
            getLogger().warning("DEBUG: " + msg);
        }
    }

    @Override
    public void pluginDisabled() {
        // TODO Auto-generated meth
    }

}
