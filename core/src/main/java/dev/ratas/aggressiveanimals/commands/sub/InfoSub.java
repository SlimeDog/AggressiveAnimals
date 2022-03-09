package dev.ratas.aggressiveanimals.commands.sub;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
    private static final String DEFAULTS = "defaults";
    private static final String NAME = "info";
    private static final String PERMS = "aggressiveanimals.info";
    private static final String USAGE = "/aggro info <mob type>";
    private static final List<String> NAMES_PLUS = Collections.unmodifiableList(getMobTypeNamesOrDefault());
    private static final List<String> OPTIONS = Collections.unmodifiableList(Arrays.asList("all"));
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
            return StringUtil.copyPartialMatches(args[0], NAMES_PLUS, list);
        }
        if (args.length == 2) {
            return StringUtil.copyPartialMatches(args[1], OPTIONS, list);
        }
        return list;
    }

    @Override
    public boolean onCommand(SDCRecipient sender, String[] args, List<String> opts) {
        if (args.length < 1) {
            return false;
        }
        MobTypeSettings settings;
        boolean showingDefaults;
        if (args[0].equalsIgnoreCase(DEFAULTS)) {
            settings = manager.getMobTypeManager().getDefaultSettings();
            showingDefaults = true;
        } else {
            MobType type = MobType.matchType(args[0]);
            if (type == null) {
                SDCSingleContextMessageFactory<String> mf = messages.getMobTypeNotFoundMessage();
                mf.getMessage(mf.getContextFactory().getContext(args[0])).sendTo(sender);
                return true;
            }
            settings = manager.getMobTypeManager().getDefinedSettings(type);
            if (settings == null) {
                SDCSingleContextMessageFactory<MobType> mf = messages.getMobTypeNotDefined();
                mf.getMessage(mf.getContextFactory().getContext(type)).sendTo(sender);
                return true;
            }
            showingDefaults = false;
        }
        boolean showFull = args.length > 1 && args[1].equalsIgnoreCase("all");
        SDCSingleContextMessageFactory<Setting<?>> nonDef = messages.getNonDefaultInfoMessagePart();
        SDCSingleContextMessageFactory<Setting<?>> def = messages.getDefaultInfoMessagePart();
        boolean ignored = false;
        boolean shownSomething = false;
        for (Setting<?> setting : settings.getAllSettings()) {
            boolean defaultVals = setting.isDefault();
            SDCSingleContextMessageFactory<Setting<?>> cur = defaultVals ? def : nonDef;
            SDCMessage<SDCSingleContext<Setting<?>>> msg = cur.getMessage(cur.getContextFactory().getContext(setting));
            if (!defaultVals || showFull || showingDefaults) {
                msg.sendTo(sender);
                shownSomething = true;
            } else {
                ignored = true;
            }
        }
        if (ignored) {
            if (!shownSomething) {
                messages.getAllDefaults().getMessage().sendTo(sender);
            } else {
                messages.getDefaultsNotShown().getMessage().sendTo(sender);
            }
        }
        return true;
    }

    private static final List<String> getMobTypeNamesOrDefault() {
        List<String> list = new ArrayList<>(MobType.names());
        list.add(DEFAULTS);
        return list;
    }

}
