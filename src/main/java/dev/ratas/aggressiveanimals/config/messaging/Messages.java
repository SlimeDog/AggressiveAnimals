package dev.ratas.aggressiveanimals.config.messaging;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.java.JavaPlugin;

public class Messages extends MessagesBase {
    private final Message reloadMessage;

    public Messages(JavaPlugin plugin) throws InvalidConfigurationException {
        super(plugin);
        this.reloadMessage = new Message(this, "reloaded-config", "Plugin was successfully reloaded");
    }

    public Message getReloadMessage() {
        return reloadMessage;
    }

}
