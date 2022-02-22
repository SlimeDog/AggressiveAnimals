package dev.ratas.aggressiveanimals.config.messaging;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.java.JavaPlugin;

public class Messages extends MessagesBase {
    private final Message reloadMessage;
    private final Message reloadFailMessage;

    public Messages(JavaPlugin plugin) throws InvalidConfigurationException {
        super(plugin);
        this.reloadMessage = new Message(this, "reloaded-config", "Plugin was successfully reloaded");
        this.reloadFailMessage = new Message(this, "problem-reloading-config",
                "There was an issue while reloading the config - check the console log");
    }

    public Message getReloadMessage() {
        return reloadMessage;
    }

    public Message getReloadFailedMessage() {
        return reloadFailMessage;
    }

}
