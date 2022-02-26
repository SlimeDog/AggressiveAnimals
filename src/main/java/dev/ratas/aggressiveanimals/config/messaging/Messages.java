package dev.ratas.aggressiveanimals.config.messaging;

import org.bukkit.configuration.InvalidConfigurationException;

import dev.ratas.aggressiveanimals.aggressive.settings.MobType;
import dev.ratas.slimedogcore.api.SlimeDogPlugin;
import dev.ratas.slimedogcore.api.messaging.SDCMessage;
import dev.ratas.slimedogcore.api.messaging.context.SDCVoidContext;
import dev.ratas.slimedogcore.api.messaging.delivery.MessageTarget;
import dev.ratas.slimedogcore.api.messaging.factory.SDCDoubleContextMessageFactory;
import dev.ratas.slimedogcore.api.messaging.factory.SDCVoidContextMessageFactory;
import dev.ratas.slimedogcore.impl.messaging.MessagesBase;
import dev.ratas.slimedogcore.impl.messaging.context.VoidContext;
import dev.ratas.slimedogcore.impl.messaging.context.factory.SingleContextFactory;
import dev.ratas.slimedogcore.impl.messaging.context.factory.VoidContextFactory;
import dev.ratas.slimedogcore.impl.messaging.context.factory.delegating.DelegatingDoubleContextFactory;
import dev.ratas.slimedogcore.impl.messaging.factory.DoubleContextMessageFactory;
import dev.ratas.slimedogcore.impl.messaging.factory.VoidContextMessageFactory;

public class Messages extends MessagesBase {
    private static final String FILE_NAME = "messages.yml";
    private SDCVoidContextMessageFactory reloadMessage;
    private SDCVoidContextMessageFactory reloadFailMessage;
    private SDCVoidContextMessageFactory listHeaderMessage;
    private SDCDoubleContextMessageFactory<MobType, Boolean> listItemMessage;
    private SDCVoidContextMessageFactory enabledMessage;
    private SDCVoidContextMessageFactory disabledMessage;

    public Messages(SlimeDogPlugin plugin) throws InvalidConfigurationException {
        super(plugin.getCustomConfigManager().getConfig(FILE_NAME));
        loadMessages();
    }

    private void loadMessages() {
        this.reloadMessage = new VoidContextMessageFactory(VoidContextFactory.INSTANCE,
                getRawMessage("reloaded-config", "Plugin was successfully reloaded"), MessageTarget.TEXT);
        this.reloadFailMessage = new VoidContextMessageFactory(VoidContextFactory.INSTANCE,
                getRawMessage("problem-reloading-config",
                        "There was an issue while reloading the config - check the console log"),
                MessageTarget.TEXT);
        this.listHeaderMessage = new VoidContextMessageFactory(VoidContextFactory.INSTANCE,
                getRawMessage("list-header", "&8Configured mobs"), MessageTarget.TEXT);
        this.listItemMessage = new DoubleContextMessageFactory<>(
                new DelegatingDoubleContextFactory<>(new SingleContextFactory<>("%mob-type%", t -> t.name()),
                        new SingleContextFactory<>("%status%", b -> {
                            SDCMessage<SDCVoidContext> m = (b ? enabledMessage : disabledMessage)
                                    .getMessage(VoidContext.INSTANCE);
                            return m.getRaw();
                        })),
                getRawMessage("list-format", "&6%mob-type% &f- %status%"), MessageTarget.TEXT);
        this.enabledMessage = new VoidContextMessageFactory(VoidContextFactory.INSTANCE,
                getRawMessage("enabled", "enabled"), MessageTarget.TEXT);
        this.disabledMessage = new VoidContextMessageFactory(VoidContextFactory.INSTANCE,
                getRawMessage("disabled", "disabled"), MessageTarget.TEXT);
    }

    public SDCVoidContextMessageFactory getReloadMessage() {
        return reloadMessage;
    }

    public SDCVoidContextMessageFactory getReloadFailedMessage() {
        return reloadFailMessage;
    }

    public SDCVoidContextMessageFactory getListHeaderMessage() {
        return listHeaderMessage;
    }

    public SDCDoubleContextMessageFactory<MobType, Boolean> getListItemMessage() {
        return listItemMessage;
    }

    public SDCVoidContextMessageFactory getEnabledMessage() {
        return enabledMessage;
    }

    public SDCVoidContextMessageFactory getDisabledMessage() {
        return disabledMessage;
    }

    public void reloadConfig() {

    }

}
