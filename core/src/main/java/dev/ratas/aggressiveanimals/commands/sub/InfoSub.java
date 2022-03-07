package dev.ratas.aggressiveanimals.commands.sub;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.util.StringUtil;

import dev.ratas.aggressiveanimals.aggressive.AggressivityManager;
import dev.ratas.aggressiveanimals.aggressive.settings.MobType;
import dev.ratas.aggressiveanimals.aggressive.settings.type.MobTypeSettings;
import dev.ratas.aggressiveanimals.aggressive.settings.type.Setting;
import dev.ratas.aggressiveanimals.config.messaging.Messages;
import dev.ratas.slimedogcore.api.messaging.SDCMessage;
import dev.ratas.slimedogcore.api.messaging.context.SDCSingleContext;
import dev.ratas.slimedogcore.api.messaging.factory.SDCSingleContextMessageFactory;
import dev.ratas.slimedogcore.api.messaging.recipient.SDCRecipient;
import dev.ratas.slimedogcore.impl.commands.AbstractSubCommand;

public class InfoSub extends AbstractSubCommand {
    private static final String NAME = "info";
    private static final String PERMS = "aggressiveanimals.info";
    private static final String USAGE = "/aggro info <mob type>";
    private final AggressivityManager manager;
    private final Messages messages;

    public InfoSub(AggressivityManager manager, Messages messages) {
        super(NAME, PERMS, USAGE);
        this.manager = manager;
        this.messages = messages;
    }

    @Override
    public List<String> onTabComplete(SDCRecipient sender, String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], MobType.names(), list);
        }
        return list;
    }

    @Override
    public boolean onCommand(SDCRecipient sender, String[] args, List<String> opts) {
        if (args.length < 1) {
            return false;
        }
        MobType type = MobType.matchType(args[0]);
        if (type == null) {
            SDCSingleContextMessageFactory<String> mf = messages.getMobTypeNotFoundMessage();
            mf.getMessage(mf.getContextFactory().getContext(args[0])).sendTo(sender);
            return true;
        }
        MobTypeSettings settings = manager.getMobTypeManager().getDefinedSettings(type);
        if (settings == null) {
            SDCSingleContextMessageFactory<MobType> mf = messages.getMobTypeNotDefined();
            mf.getMessage(mf.getContextFactory().getContext(type)).sendTo(sender);
            return true;
        }
        SDCSingleContextMessageFactory<Setting<?>> nonDef = messages.getNonDefaultInfoMessagePart();
        SDCSingleContextMessageFactory<Setting<?>> def = messages.getDefaultInfoMessagePart();
        for (Setting<?> setting : settings.getAllSettings()) {
            SDCSingleContextMessageFactory<Setting<?>> cur = setting.isDefault() ? def : nonDef;
            SDCMessage<SDCSingleContext<Setting<?>>> msg = cur.getMessage(cur.getContextFactory().getContext(setting));
            if (!msg.getFilled().isEmpty()) {
                msg.sendTo(sender);
            }
        }
        return true;
    }

}
