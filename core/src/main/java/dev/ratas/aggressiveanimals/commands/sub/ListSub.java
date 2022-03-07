package dev.ratas.aggressiveanimals.commands.sub;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.bukkit.util.StringUtil;

import dev.ratas.aggressiveanimals.aggressive.settings.MobType;
import dev.ratas.aggressiveanimals.aggressive.settings.MobTypeManager;
import dev.ratas.aggressiveanimals.aggressive.settings.type.MobTypeSettings;
import dev.ratas.aggressiveanimals.config.messaging.Messages;
import dev.ratas.aggressiveanimals.utils.Paginator;
import dev.ratas.slimedogcore.api.messaging.context.factory.SDCSingleContextFactory;
import dev.ratas.slimedogcore.api.messaging.factory.SDCSingleContextMessageFactory;
import dev.ratas.slimedogcore.api.messaging.recipient.SDCPlayerRecipient;
import dev.ratas.slimedogcore.api.messaging.recipient.SDCRecipient;
import dev.ratas.slimedogcore.impl.commands.AbstractSubCommand;

public class ListSub extends AbstractSubCommand {
    private static final int PER_PAGE = 8;
    private static final Pattern NUMBER_PATTERN = Pattern.compile("\\d*");
    private static final String NAME = "list";
    private static final String USAGE = "/aggro list [enabled | disabled] [page]";
    private static final String PERMS = "aggressiveanimals.list";
    private static final List<String> OPTIONS = Collections.unmodifiableList(Arrays.asList("enabled", "disabled"));
    // Currently (as of 1.18.2), 4 is the lasst page. But this may change in the
    // future
    private static final List<String> OPTION_PAGES = Collections.unmodifiableList(Arrays.asList("1", "2", "3", "4"));
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
            if (!(sender instanceof SDCPlayerRecipient)) {
                return StringUtil.copyPartialMatches(args[0], OPTIONS, new ArrayList<>());
            }
            List<String> options = new ArrayList<>(OPTIONS);
            options.addAll(OPTION_PAGES);
            return StringUtil.copyPartialMatches(args[0], options, new ArrayList<>());
        }
        if (args.length == 2 && (sender instanceof SDCPlayerRecipient)) {
            if (NUMBER_PATTERN.matcher(args[0]).matches()) {
                return StringUtil.copyPartialMatches(args[1], OPTIONS, new ArrayList<>());
            } else {
                return StringUtil.copyPartialMatches(args[1], OPTION_PAGES, new ArrayList<>());
            }
        }
        return Collections.emptyList();
    }

    @Override
    public boolean onCommand(SDCRecipient sender, String[] args, List<String> opts) {
        SettingTarget target;
        int page;
        if (args.length == 0) {
            target = SettingTarget.BOTH;
            page = 1;
        } else if (NUMBER_PATTERN.matcher(args[0]).matches()) {
            if (args.length > 1) {
                target = getTarget(args[1]);
            } else {
                target = SettingTarget.BOTH;
            }
            page = Integer.parseInt(args[0]);
        } else {
            if (args.length > 1) {
                if (NUMBER_PATTERN.matcher(args[1]).matches()) {
                    page = Integer.parseInt(args[1]);
                    target = getTarget(args[0]);
                } else {
                    return false; // garbage
                }
            } else { // only target specified
                target = getTarget(args[0]);
                page = 1;
            }
        }
        if (target == null) {
            return false;
        }
        int perPage = (sender instanceof SDCPlayerRecipient) ? PER_PAGE : Integer.MAX_VALUE;

        SDCSingleContextMessageFactory<MobType> lmf;
        Paginator<MobTypeSettings> paginator;
        try {
            paginator = new Paginator<>(target.getSettings(manager), page, perPage);
        } catch (IllegalArgumentException e) {
            SDCSingleContextMessageFactory<Integer> msg = messages.getNoSuchPageMessage();
            msg.getMessage(msg.getContextFactory().getContext(page)).sendTo(sender);
            return true;
        }
        if (perPage == PER_PAGE) { // in game
            SDCSingleContextMessageFactory<Paginator<?>> msg = messages.getPagedListHeaderMessage();
            msg.getMessage(msg.getContextFactory().getContext(paginator)).sendTo(sender);
        } else {
            messages.getListHeaderMessage().getMessage().sendTo(sender);
        }
        for (MobTypeSettings settings : paginator.getOnPage()) {
            if (!target.shouldInclude(settings)) {
                // this should no longer happen
                continue; // ignore
            }
            lmf = messages.getListItemMessage(settings.enabled());
            SDCSingleContextFactory<MobType> cf = lmf.getContextFactory();
            lmf.getMessage(cf.getContext(settings.entityType())).sendTo(sender);
        }
        return true;
    }

    private final SettingTarget getTarget(String arg) {
        if (arg.equalsIgnoreCase("enabled")) {
            return SettingTarget.ENABLED;
        } else if (arg.equalsIgnoreCase("disabled")) {
            return SettingTarget.DISABLED;
        }
        return null;
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

        public List<MobTypeSettings> getSettings(MobTypeManager manager) {
            switch (this) {
                case BOTH:
                    return (List<MobTypeSettings>) manager.getUsedSettings();
                case ENABLED:
                    return (List<MobTypeSettings>) manager.getEnabledSettings();
                case DISABLED:
                    return (List<MobTypeSettings>) manager.getDisabledSettings();
            }
            throw new IllegalStateException("Unknown type of SettingTarget: " + this);
        }
    }

}
