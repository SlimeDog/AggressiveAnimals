package dev.ratas.aggressiveanimals.commands.sub;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.util.StringUtil;

import dev.ratas.aggressiveanimals.aggressive.settings.MobType;
import dev.ratas.aggressiveanimals.aggressive.settings.MobTypeManager;
import dev.ratas.aggressiveanimals.aggressive.settings.type.MobTypeSettings;
import dev.ratas.aggressiveanimals.config.messaging.Messages;
import dev.ratas.slimedogcore.api.messaging.context.factory.SDCSingleContextFactory;
import dev.ratas.slimedogcore.api.messaging.factory.SDCSingleContextMessageFactory;
import dev.ratas.slimedogcore.api.messaging.recipient.SDCRecipient;
import dev.ratas.slimedogcore.impl.commands.AbstractSubCommand;

public class ListSub extends AbstractSubCommand {
    private static final String NAME = "list";
    private static final String USAGE = "/aggro list [enabled | disabled]";
    private static final String PERMS = "aggressiveanimals.list";
    private static final List<String> OPTIONS = Collections.unmodifiableList(Arrays.asList("enabled", "disabled"));
    private final MobTypeManager manager;
    private final Messages messages;

    public ListSub(MobTypeManager manager, Messages messages) {
        super(NAME, PERMS, USAGE);
        this.manager = manager;
        this.messages = messages;
    }

    @Override
    public List<String> onTabComplete(SDCRecipient sender, String[] args) {
        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], OPTIONS, new ArrayList<>());
        }
        return Collections.emptyList();
    }

    @Override
    public boolean onCommand(SDCRecipient sender, String[] args, List<String> opts) {
        SettingTarget target;
        if (args.length == 0) {
            target = SettingTarget.BOTH;
        } else if (args[0].equalsIgnoreCase("enabled")) {
            target = SettingTarget.ENABLED;
        } else if (args[0].equalsIgnoreCase("disabled")) {
            target = SettingTarget.DISABLED;
        } else {
            return false; // unknown option
        }
        messages.getListHeaderMessage().getMessage().sendTo(sender);
        SDCSingleContextMessageFactory<MobType> lmf;
        for (MobTypeSettings settings : manager.getUsedSettings()) {
            if (!target.shouldInclude(settings)) {
                continue; // ignore
            }
            lmf = messages.getListItemMessage(settings.enabled());
            SDCSingleContextFactory<MobType> cf = lmf.getContextFactory();
            lmf.getMessage(cf.getContext(settings.entityType())).sendTo(sender);
        }
        return true;
    }

    private enum SettingTarget {
        ENABLED, DISABLED, BOTH;

        public boolean shouldInclude(MobTypeSettings settings) {
            switch (this) {
                case BOTH:
                    return true;
                case ENABLED:
                    return settings.enabled();
                case DISABLED:
                    return !settings.enabled();
            }
            throw new IllegalStateException("Unknown type of SettingTarget: " + this);
        }
    }

}
