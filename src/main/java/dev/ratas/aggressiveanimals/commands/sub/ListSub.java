package dev.ratas.aggressiveanimals.commands.sub;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;

import dev.ratas.aggressiveanimals.aggressive.settings.MobType;
import dev.ratas.aggressiveanimals.aggressive.settings.MobTypeManager;
import dev.ratas.aggressiveanimals.aggressive.settings.type.MobTypeSettings;
import dev.ratas.aggressiveanimals.commands.SubCommand;
import dev.ratas.aggressiveanimals.config.messaging.Messages;
import dev.ratas.aggressiveanimals.config.messaging.context.Context;
import dev.ratas.aggressiveanimals.config.messaging.context.factory.DoubleContextFactory;
import dev.ratas.aggressiveanimals.config.messaging.message.MessageFactory;

public class ListSub extends SubCommand {
    private static final String NAME = "list";
    private static final String USAGE = "/aggro list [enabled | disabled]";
    private static final String PERMS = "aggressiveanimals.list";
    private static final List<String> OPTIONS = Collections.unmodifiableList(Arrays.asList("enabled", "disabled"));
    private final MobTypeManager manager;
    private final Messages messages;

    public ListSub(MobTypeManager manager, Messages messages) {
        super(NAME, USAGE, PERMS);
        this.manager = manager;
        this.messages = messages;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], OPTIONS, new ArrayList<>());
        }
        return Collections.emptyList();
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
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
        messages.getListHeaderMessage().getMessage(Context.NULL).sendTo(sender);
        MessageFactory.DoubleFactory<MobType, Boolean> lmf = messages.getListItemMessage();
        for (MobTypeSettings settings : manager.getUsedSettings()) {
            if (!target.shouldInclude(settings)) {
                continue; // ignore
            }
            DoubleContextFactory<MobType, Boolean> cf = lmf.getContextFactory();
            lmf.getMessage(cf.getContext(settings.entityType(), settings.enabled())).sendTo(sender);
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
