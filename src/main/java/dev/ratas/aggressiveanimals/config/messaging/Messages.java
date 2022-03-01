package dev.ratas.aggressiveanimals.config.messaging;

import org.bukkit.configuration.InvalidConfigurationException;

import dev.ratas.aggressiveanimals.aggressive.settings.MobType;
import dev.ratas.slimedogcore.api.SlimeDogPlugin;
import dev.ratas.slimedogcore.api.messaging.SDCMessage;
import dev.ratas.slimedogcore.api.messaging.context.SDCVoidContext;
import dev.ratas.slimedogcore.api.messaging.factory.SDCDoubleContextMessageFactory;
import dev.ratas.slimedogcore.api.messaging.factory.SDCVoidContextMessageFactory;
import dev.ratas.slimedogcore.impl.messaging.MessagesBase;
import dev.ratas.slimedogcore.impl.messaging.context.VoidContext;
import dev.ratas.slimedogcore.impl.messaging.factory.MsgUtil;

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
        this.reloadMessage = MsgUtil.voidContext(getRawMessage("reloaded-config", "Plugin was successfully reloaded"));
        this.reloadFailMessage = MsgUtil.voidContext(getRawMessage("problem-reloading-config",
                "There was an issue while reloading the config - check the console log"));
        this.listHeaderMessage = MsgUtil.voidContext(getRawMessage("list-header", "&8Configured mobs"));
        this.listItemMessage = MsgUtil.doubleContext("%mob-type%", t -> t.name(), "%status%", b -> {
            SDCMessage<SDCVoidContext> m = (b ? enabledMessage : disabledMessage).getMessage(VoidContext.INSTANCE);
            return m.getFilled();
        }, getRawMessage("list-format", "&6%mob-type% &f- %status%"));
        this.enabledMessage = MsgUtil.voidContext(getRawMessage("enabled", "enabled"));
        this.disabledMessage = MsgUtil.voidContext(getRawMessage("disabled", "disabled"));
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
