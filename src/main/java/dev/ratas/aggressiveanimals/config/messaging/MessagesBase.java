package dev.ratas.aggressiveanimals.config.messaging;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.java.JavaPlugin;

import dev.ratas.aggressiveanimals.config.CustomConfigHandler;

public class MessagesBase extends CustomConfigHandler {
    private final static String FILE_NAME = "messages.yml";
    private final JavaPlugin plugin;

    public MessagesBase(JavaPlugin plugin) throws InvalidConfigurationException {
        super(plugin, FILE_NAME);
        saveDefaultConfig();
        this.plugin = plugin;
    }

    public JavaPlugin getOwner() {
        return plugin;
    }

}
