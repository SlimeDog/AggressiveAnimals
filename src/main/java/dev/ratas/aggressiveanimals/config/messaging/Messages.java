package dev.ratas.aggressiveanimals.config.messaging;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.java.JavaPlugin;

import dev.ratas.aggressiveanimals.aggressive.settings.MobType;
import dev.ratas.aggressiveanimals.config.messaging.context.Context;
import dev.ratas.aggressiveanimals.config.messaging.context.DoubleContext;
import dev.ratas.aggressiveanimals.config.messaging.context.Context.VoidContext;
import dev.ratas.aggressiveanimals.config.messaging.context.factory.ContextFactory;
import dev.ratas.aggressiveanimals.config.messaging.context.factory.DelegatingDoubleContextFactory;
import dev.ratas.aggressiveanimals.config.messaging.context.factory.SingleContextFactory;
import dev.ratas.aggressiveanimals.config.messaging.message.DoubleContextMessage;
import dev.ratas.aggressiveanimals.config.messaging.message.Message;
import dev.ratas.aggressiveanimals.config.messaging.message.MessageFactory;
import dev.ratas.aggressiveanimals.config.messaging.message.VoidContextMessage;

public class Messages extends MessagesBase {
    private MessageFactory<VoidContext> reloadMessage;
    private MessageFactory<VoidContext> reloadFailMessage;
    private MessageFactory<VoidContext> listHeaderMessage;
    private MessageFactory.DoubleFactory<MobType, Boolean> listItemMessage;
    private MessageFactory<VoidContext> enabledMessage;
    private MessageFactory<VoidContext> disabledMessage;

    public Messages(JavaPlugin plugin) throws InvalidConfigurationException {
        super(plugin);
        loadMessages();
    }

    private void loadMessages() {
        this.reloadMessage = new MessageFactory<>(VoidContextMessage.class, VoidContext.class, ContextFactory.NULL,
                getRawMessage("reloaded-config", "Plugin was successfully reloaded"));
        this.reloadFailMessage = new MessageFactory<>(VoidContextMessage.class, VoidContext.class, ContextFactory.NULL,
                getRawMessage("problem-reloading-config",
                        "There was an issue while reloading the config - check the console log"));
        this.listHeaderMessage = new MessageFactory<>(VoidContextMessage.class, VoidContext.class, ContextFactory.NULL,
                getRawMessage("list-header", "&8Configured mobs"));
        this.listItemMessage = new MessageFactory.DoubleFactory<>(DoubleContextMessage.class, DoubleContext.class,
                new DelegatingDoubleContextFactory<>(new SingleContextFactory<>("%mob-type%", t -> t.name()),
                        new SingleContextFactory<>("%status%",
                                b -> {
                                    Message<VoidContext> m = (b ? enabledMessage : disabledMessage)
                                            .getMessage(Context.NULL);
                                    return m.getRaw();
                                })),
                getRawMessage("list-format", "&6%mob-type% &f- %status%"));
        this.enabledMessage = new MessageFactory<>(VoidContextMessage.class, VoidContext.class, ContextFactory.NULL,
                getRawMessage("enabled", "enabled"));
        this.disabledMessage = new MessageFactory<>(VoidContextMessage.class, VoidContext.class, ContextFactory.NULL,
                getRawMessage("disabled", "disabled"));
    }

    public MessageFactory<VoidContext> getReloadMessage() {
        return reloadMessage;
    }

    public MessageFactory<VoidContext> getReloadFailedMessage() {
        return reloadFailMessage;
    }

    public MessageFactory<VoidContext> getListHeaderMessage() {
        return listHeaderMessage;
    }

    public MessageFactory.DoubleFactory<MobType, Boolean> getListItemMessage() {
        return listItemMessage;
    }

    public MessageFactory<VoidContext> getEnabledMessage() {
        return enabledMessage;
    }

    public MessageFactory<VoidContext> getDisabledMessage() {
        return disabledMessage;
    }

}
