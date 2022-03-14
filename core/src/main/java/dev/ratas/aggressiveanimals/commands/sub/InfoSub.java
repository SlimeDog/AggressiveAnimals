package dev.ratas.aggressiveanimals.commands.sub;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.bukkit.util.StringUtil;

import dev.ratas.aggressiveanimals.aggressive.AggressivityManager;
import dev.ratas.aggressiveanimals.aggressive.settings.MobType;
import dev.ratas.aggressiveanimals.aggressive.settings.type.MobTypeSettings;
import dev.ratas.aggressiveanimals.aggressive.settings.type.Setting;
import dev.ratas.aggressiveanimals.config.messaging.Messages;
import dev.ratas.slimedogcore.api.messaging.SDCMessage;
import dev.ratas.slimedogcore.api.messaging.context.SDCSingleContext;
import dev.ratas.slimedogcore.api.messaging.factory.SDCDoubleContextMessageFactory;
import dev.ratas.slimedogcore.api.messaging.factory.SDCSingleContextMessageFactory;
import dev.ratas.slimedogcore.api.messaging.recipient.SDCPlayerRecipient;
import dev.ratas.slimedogcore.api.messaging.recipient.SDCRecipient;
import dev.ratas.slimedogcore.impl.commands.AbstractSubCommand;
import dev.ratas.slimedogcore.impl.utils.Paginator;

public class InfoSub extends AbstractSubCommand {
    private static final int PER_PAGE = 8;
    private static final String DEFAULTS = "defaults";
    private static final String NAME = "info";
    private static final String PERMS = "aggressiveanimals.info";
    private static final String USAGE = "/aggro info <mob type> [all] [page]";
    private static final List<String> NAMES_PLUS = Collections.unmodifiableList(getMobTypeNamesOrDefault());
    private static final List<String> OPTIONS = Collections.unmodifiableList(Arrays.asList("all"));
    private static final List<String> PAGES = Collections.unmodifiableList(Arrays.asList("1", "2", "3", "4"));
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
            if (args[0].equalsIgnoreCase(DEFAULTS)) {
                return StringUtil.copyPartialMatches(args[1], PAGES, list);
            }
            return StringUtil.copyPartialMatches(args[1], OPTIONS, list);
        }
        if (args.length == 3 && (sender instanceof SDCPlayerRecipient) && args[1].equalsIgnoreCase("all")) {
            return StringUtil.copyPartialMatches(args[2], PAGES, list);
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
            settings = manager.getMobTypeManager().getConfigDefaultSettings();
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
        int page = 1;
        if (sender instanceof SDCPlayerRecipient) {
            String toParse;
            if (showFull && args.length > 2) {
                toParse = args[2];
            } else if (showingDefaults && args.length > 1) {
                toParse = args[1];
            } else {
                toParse = null;
            }
            if (toParse != null) {
                try {
                    page = Integer.parseInt(toParse);
                } catch (IllegalArgumentException e) {
                    return false; // garbage
                }
            }
        }
        SDCSingleContextMessageFactory<Setting<?>> nonDef = showFull ? messages.getNonDefaultInfoMessagePartInAll()
                : messages.getNonDefaultInfoMessagePart();
        SDCSingleContextMessageFactory<Setting<?>> def = messages.getDefaultInfoMessagePart();
        AtomicBoolean ignored = new AtomicBoolean(false);
        int perPage;
        if ((showFull || showingDefaults) && sender instanceof SDCPlayerRecipient) {
            perPage = PER_PAGE;
        } else {
            perPage = Integer.MAX_VALUE;
        }
        Paginator<Setting<?>> paginator;
        try {
            paginator = new Paginator<>(settings.getAllSettings(), page, perPage);
        } catch (IllegalArgumentException e) {
            SDCSingleContextMessageFactory<Integer> msg = messages.getNoSuchPageMessage();
            msg.getMessage(msg.getContextFactory().getContext(page)).sendTo(sender);
            return true;
        }
        AtomicBoolean shownSomething = new AtomicBoolean(false);
        if (perPage == PER_PAGE) { // in game and showing all
            SDCDoubleContextMessageFactory<MobType, Paginator<Setting<?>>> header = messages.getPagedInfoHeader();
            header.getMessage(header.getContextFactory().getContext(settings.entityType().value(), paginator))
                    .sendTo(sender);
        } else { // console
            SDCSingleContextMessageFactory<MobType> header = messages.getInfoHeader();
            header.getMessage(header.getContextFactory().getContext(settings.entityType().value())).sendTo(sender);
        }
        for (Setting<?> setting : paginator.getOnPage()) {
            boolean defaultVals;
            defaultVals = setting.isDefault();
            SDCSingleContextMessageFactory<Setting<?>> cur = defaultVals ? def : nonDef;
            SDCMessage<SDCSingleContext<Setting<?>>> msg = cur.getMessage(cur.getContextFactory().getContext(setting));
            if (!defaultVals || showFull || showingDefaults) {
                msg.sendTo(sender);
                shownSomething.set(true);
            } else {
                ignored.set(true);
            }
        }
        if (ignored.get()) {
            if (!shownSomething.get()) {
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
