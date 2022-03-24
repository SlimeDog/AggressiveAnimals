package dev.ratas.aggressiveanimals.main;

import org.bstats.bukkit.Metrics;
import org.bukkit.configuration.InvalidConfigurationException;

import dev.ratas.aggressiveanimals.IAggressiveAnimals;
import dev.ratas.aggressiveanimals.aggressive.AggressivityManager;
import dev.ratas.aggressiveanimals.aggressive.reasons.PacificationReason;
import dev.ratas.aggressiveanimals.commands.AggressiveAnimalsCommand;
import dev.ratas.aggressiveanimals.config.ConfigLoadIssueResolver;
import dev.ratas.aggressiveanimals.config.Settings;
import dev.ratas.aggressiveanimals.config.messaging.Messages;
import dev.ratas.aggressiveanimals.hooks.npc.NPCHookManager;
import dev.ratas.aggressiveanimals.listeners.AggressionListener;
import dev.ratas.aggressiveanimals.listeners.MobRegistrationListener;
import dev.ratas.aggressiveanimals.nms.NMSResolver;
import dev.ratas.slimedogcore.api.config.SDCCustomConfig;
import dev.ratas.slimedogcore.api.config.exceptions.ConfigException;
import dev.ratas.slimedogcore.impl.SlimeDogCore;
import dev.ratas.slimedogcore.impl.utils.UpdateChecker;

public class AggressiveAnimals extends SlimeDogCore implements IAggressiveAnimals {
    private static final int SPIGOT_ID = 100934;
    private static final int BSTATS_ID = 14423;
    private SDCCustomConfig config;
    private Messages messages;
    private Settings settings;
    private NPCHookManager npcHookManager;
    private AggressivityManager aggressivityManager;
    private MobRegistrationListener registrationListener;

    private void loadDataFromFile() {
        ConfigLoadIssueResolver issues = ConfigLoadIssueResolver.atLoad();
        try {
            config = getDefaultConfig();
            config.saveDefaultConfig();
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
        try {
            aggressivityManager = new AggressivityManager(this, settings, npcHookManager, NMSResolver.getSetter(this));
        } catch (ConfigException e) {
            issues.logIssue("INVALID MOB SETTINGS", "Invalid mob settings - disabling", e);
            disableMe(issues);
            return;
        }
    }

    @Override
    public void pluginEnabled() {
        npcHookManager = new NPCHookManager();
        loadDataFromFile();
        registrationListener = new MobRegistrationListener(this, aggressivityManager);
        getPluginManager().registerEvents(registrationListener);
        getPluginManager().registerEvents(new AggressionListener(this, aggressivityManager));
        // bstats
        if (settings.enableMetrics()) {
            new Metrics(this, BSTATS_ID);
        }
        // commands
        getCommand("aggressiveanimals").setExecutor(new AggressiveAnimalsCommand(this, messages));
        attemptUpdateCheck();
    }

    private void attemptUpdateCheck() {
        if (settings.checkForUpdates()) {
            new UpdateChecker(this, (response, version) -> {
                switch (response) {
                    case LATEST:
                        getLogger().info("You are running the latest version");
                        break;
                    case FOUND_NEW:
                        getLogger().info("A new version " + version + " is available for download");
                        break;
                    case UNAVAILABLE:
                        getLogger().info("Version update information is not available at this time");
                        break;
                }
            }, SPIGOT_ID).check();
        }
    }

    @Override
    public ConfigLoadIssueResolver reload() {
        ConfigLoadIssueResolver issues = ConfigLoadIssueResolver.atReload();
        try {
            config.reloadConfig();
        } catch (ConfigException e) {
            issues.logIssue("INVALID CONFIGURATION", "Invalid configuration - disabling", e);
            disableMe(issues, true);
            return issues;
        }
        try {
            messages.reloadConfig();
        } catch (ConfigException e) {
            issues.logIssue("INVALID CONFIGURATION", "Invalid configuration - disabling", e);
            disableMe(issues, true);
            return issues;
        }
        try {
            aggressivityManager.reload(settings);
        } catch (ConfigException e) {
            issues.logIssue("INVALID MOB SETTINGS", "Invalid mob settings - disabling", e);
            disableMe(issues, true);
            return issues;
        }
        registrationListener.onReload();
        attemptUpdateCheck();
        return issues;
    }

    public Messages getMessages() {
        return messages;
    }

    public Settings getSettings() {
        return settings;
    }

    @Override
    public NPCHookManager getNPCHookManager() {
        return npcHookManager;
    }

    @Override
    public AggressivityManager getAggressivityManager() {
        return aggressivityManager;
    }

    private void disableMe(ConfigLoadIssueResolver issues) {
        disableMe(issues, false);
    }

    private void disableMe(ConfigLoadIssueResolver issues, boolean nextTick) {
        getLogger().severe(issues.asString());
        Runnable disabler = () -> getServer().getPluginManager().disablePlugin(this);
        if (nextTick) {
            getScheduler().runTask(disabler);
        } else {
            disabler.run();
        }
    }

    @Override
    public void pluginDisabled() {
        if (aggressivityManager != null) {
            aggressivityManager.unregisterAll(PacificationReason.PLUGIN_DISABLE);
        }
    }

}
