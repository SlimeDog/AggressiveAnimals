package dev.ratas.aggressiveanimals.config.messaging;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.java.JavaPlugin;

public class Messages extends MessagesBase {
    private final Message reloadMessage;
    private final Message reloadFailMessage;
    private final Message listHeaderMessage;
    private final Message listItemMessage;
    private final Message enabledMessage;
    private final Message disabledMessage;

    public Messages(JavaPlugin plugin) throws InvalidConfigurationException {
        super(plugin);
        this.reloadMessage = new Message(this, "reloaded-config", "Plugin was successfully reloaded");
        this.reloadFailMessage = new Message(this, "problem-reloading-config",
                "There was an issue while reloading the config - check the console log");
        this.listHeaderMessage = new Message(this, "list-header", "&8Configured mobs");
        this.listItemMessage = new Message(this, "list-format", "&6%mob-type% &f- %status%");
        this.enabledMessage = new Message(this, "enabled", "enabled");
        this.disabledMessage = new Message(this, "disabled", "disabled");
    }

    public Message getReloadMessage() {
        return reloadMessage;
    }

    public Message getReloadFailedMessage() {
        return reloadFailMessage;
    }

    public Message getListHeaderMessage() {
        return listHeaderMessage;
    }

    public Message getListItemMessage() {
        return listItemMessage;
    }

    public Message getEnabledMessage() {
        return enabledMessage;
    }

    public Message getDisabledMessage() {
        return disabledMessage;
    }

}
