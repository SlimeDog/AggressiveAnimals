package dev.ratas.aggressiveanimals.config.messaging;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import org.bukkit.configuration.InvalidConfigurationException;

import dev.ratas.aggressiveanimals.aggressive.settings.MobType;
import dev.ratas.aggressiveanimals.aggressive.settings.type.Setting;
import dev.ratas.aggressiveanimals.utils.Paginator;
import dev.ratas.slimedogcore.api.SlimeDogPlugin;
import dev.ratas.slimedogcore.api.messaging.context.factory.SDCDoubleContextFactory;
import dev.ratas.slimedogcore.api.messaging.context.factory.SDCSingleContextFactory;
import dev.ratas.slimedogcore.api.messaging.delivery.MessageTarget;
import dev.ratas.slimedogcore.api.messaging.factory.SDCDoubleContextMessageFactory;
import dev.ratas.slimedogcore.api.messaging.factory.SDCSingleContextMessageFactory;
import dev.ratas.slimedogcore.api.messaging.factory.SDCVoidContextMessageFactory;
import dev.ratas.slimedogcore.impl.messaging.MessagesBase;
import dev.ratas.slimedogcore.impl.messaging.context.factory.SingleContextFactory;
import dev.ratas.slimedogcore.impl.messaging.context.factory.delegating.DelegatingDoubleContextFactory;
import dev.ratas.slimedogcore.impl.messaging.context.factory.delegating.DelegatingMultipleToOneContextFactory;
import dev.ratas.slimedogcore.impl.messaging.factory.DoubleContextMessageFactory;
import dev.ratas.slimedogcore.impl.messaging.factory.MsgUtil;

public class Messages extends MessagesBase {
    private static final String FILE_NAME = "messages.yml";
    private SDCVoidContextMessageFactory reloadMessage;
    private SDCVoidContextMessageFactory reloadFailMessage;
    private SDCVoidContextMessageFactory listHeaderMessage;
    private SDCSingleContextMessageFactory<Paginator<?>> pagedListHeaderMessage;
    private SDCSingleContextMessageFactory<MobType> listEnabledItemMessage;
    private SDCSingleContextMessageFactory<MobType> listDisabledItemMessage;
    private SDCVoidContextMessageFactory enabledMessage;
    private SDCVoidContextMessageFactory disabledMessage;
    private SDCSingleContextMessageFactory<String> mobTypeNotFoundMessage;
    private SDCSingleContextMessageFactory<MobType> mobTypeNotDefined;
    private SDCSingleContextMessageFactory<Setting<?>> infoNonDefaultMessagePart;
    private SDCSingleContextMessageFactory<Setting<?>> infoNonDefaultMessagePartInAll;
    private SDCSingleContextMessageFactory<Setting<?>> infoDefaultMessagePart;
    private SDCSingleContextMessageFactory<Integer> noSuchPageMessage;
    private SDCVoidContextMessageFactory defaultsNotShown;
    private SDCVoidContextMessageFactory allDefaults;
    private SDCSingleContextMessageFactory<MobType> infoHeader;
    private SDCDoubleContextMessageFactory<MobType, Paginator<Setting<?>>> pagedInfoHeader;

    public Messages(SlimeDogPlugin plugin) throws InvalidConfigurationException {
        super(plugin.getCustomConfigManager().getConfig(FILE_NAME));
        loadMessages();
    }

    private void loadMessages() {
        this.reloadMessage = MsgUtil.voidContext(getRawMessage("reloaded-config", "Plugin was successfully reloaded"));
        this.reloadFailMessage = MsgUtil.voidContext(getRawMessage("problem-reloading-config",
                "There was an issue while reloading the config - check the console log"));
        this.listHeaderMessage = MsgUtil.voidContext(getRawMessage("list-header", "&8Configured mobs"));
        MsgUtil.MultipleToOneBuilder<Paginator<?>> pagedHeaderBuilder = new MsgUtil.MultipleToOneBuilder<>(
                getRawMessage("list-header-paged", "Configured mobs - page %page% (%start%-%end%)"));
        pagedHeaderBuilder.with("%page%", p -> String.valueOf(p.getPage()));
        pagedHeaderBuilder.with("%start%", p -> String.valueOf(p.getPageStart() + 1));
        pagedHeaderBuilder.with("%end%", p -> String.valueOf(p.getPageEnd()));
        this.pagedListHeaderMessage = pagedHeaderBuilder.build();
        this.listEnabledItemMessage = MsgUtil.singleContext("%mob-type%", t -> t.name(),
                getRawMessage("list-format-enabled", "&a%mob-type% - enabled"));
        this.listDisabledItemMessage = MsgUtil.singleContext("%mob-type%", t -> t.name(),
                getRawMessage("list-format-disabled", "%mob-type% - disabled"));
        this.enabledMessage = MsgUtil.voidContext(getRawMessage("enabled", "enabled"));
        this.disabledMessage = MsgUtil.voidContext(getRawMessage("disabled", "disabled"));
        this.mobTypeNotFoundMessage = MsgUtil.singleContext("%mob-type%", t -> t,
                getRawMessage("mob-type-not-found", "Mob type not found: %mob-type%"));
        this.mobTypeNotDefined = MsgUtil.singleContext("%mob-type%", mt -> mt.name(),
                getRawMessage("mob-type-not-defined", "No information defined for mon type: %mob-type%"));
        this.noSuchPageMessage = MsgUtil.singleContext("%page%", p -> String.valueOf(p),
                getRawMessage("no-such-page", "Page does not exist: %page%"));
        Function<Setting<?>, String> valueParser = s -> {
            Object val = s.value();
            if (val instanceof Double) {
                return formatDouble((double) val);
            } else if (val instanceof Float) {
                return formatDouble((float) val);
            } else if (val instanceof List) {
                @SuppressWarnings("unchecked")
                List<String> list = (List<String>) val;
                if (list.isEmpty()) {
                    if (s.path().equals("enabled-worlds")) {
                        return "all";
                    } else {
                        return "none";
                    }
                } else {
                    return String.join(", ", list);
                }
            }
            return String.valueOf(val);
        };
        MsgUtil.MultipleToOneBuilder<Setting<?>> builder2 = new MsgUtil.MultipleToOneBuilder<>(
                getRawMessage("info-non-default-setting", "%setting-path%: %setting-value%"));
        builder2.with("%setting-path%", s -> s.path());
        builder2.with("%setting-value%", valueParser);
        infoNonDefaultMessagePart = builder2.build();
        MsgUtil.MultipleToOneBuilder<Setting<?>> builderNDA = new MsgUtil.MultipleToOneBuilder<>(
                getRawMessage("info-non-default-setting-in-all", "&a%setting-path%: %setting-value%"));
        builderNDA.with("%setting-path%", s -> s.path());
        builderNDA.with("%setting-value%", valueParser);
        infoNonDefaultMessagePartInAll = builderNDA.build();
        MsgUtil.MultipleToOneBuilder<Setting<?>> builder3 = new MsgUtil.MultipleToOneBuilder<>(
                getRawMessage("info-default-setting", "%setting-path%: %default-value%"));
        builder3.with("%setting-path%", s -> s.path());
        builder3.with("%default-value%", valueParser);
        infoDefaultMessagePart = builder3.build();
        defaultsNotShown = MsgUtil.voidContext(getRawMessage("info-default-settings-not-shown",
                "{\"extra\":[{\"text\":\"Remaining settings are defaults; type \"},{\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/aggro info defaults\"},\"text\":\"/aggro info defaults\"}],\"text\":\"\"}"));
        allDefaults = MsgUtil.voidContext(getRawMessage("info-all-defaults-not-shown",
                "{\"extra\":[{\"text\":\"All settings are defaults; type \"},{\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/aggro info defaults\"},\"text\":\"/aggro info defaults\"}],\"text\":\"\"}"));
        infoHeader = MsgUtil.singleContext("%mob-type%", mt -> mt.name(),
                getRawMessage("info-header", "Info for %mob-type%"));
        SDCSingleContextFactory<MobType> mtcf = new SingleContextFactory<>("%mob-type%", mt -> mt.name());
        SDCSingleContextFactory<Paginator<Setting<?>>> pcf = new DelegatingMultipleToOneContextFactory<>(
                new SingleContextFactory<>("%page%", p -> String.valueOf(p.getPage())),
                new SingleContextFactory<>("%start%", p -> String.valueOf(p.getPageStart())),
                new SingleContextFactory<>("%end%", p -> String.valueOf(p.getPageEnd())));
        SDCDoubleContextFactory<MobType, Paginator<Setting<?>>> cf = new DelegatingDoubleContextFactory<>(mtcf, pcf);
        pagedInfoHeader = new DoubleContextMessageFactory<>(cf,
                getRawMessage("info-header-paged", "Info for %mob-type% - page %page% (%start%-%end%)"),
                MessageTarget.TEXT);
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

    public SDCSingleContextMessageFactory<Paginator<?>> getPagedListHeaderMessage() {
        return pagedListHeaderMessage;
    }

    public SDCSingleContextMessageFactory<MobType> getListItemMessage(boolean enabled) {
        return enabled ? listEnabledItemMessage : listDisabledItemMessage;
    }

    public SDCVoidContextMessageFactory getEnabledMessage() {
        return enabledMessage;
    }

    public SDCVoidContextMessageFactory getDisabledMessage() {
        return disabledMessage;
    }

    public SDCSingleContextMessageFactory<String> getMobTypeNotFoundMessage() {
        return mobTypeNotFoundMessage;
    }

    public SDCSingleContextMessageFactory<MobType> getMobTypeNotDefined() {
        return mobTypeNotDefined;
    }

    public SDCSingleContextMessageFactory<Setting<?>> getNonDefaultInfoMessagePart() {
        return infoNonDefaultMessagePart;
    }

    public SDCSingleContextMessageFactory<Setting<?>> getNonDefaultInfoMessagePartInAll() {
        return infoNonDefaultMessagePartInAll;
    }

    public SDCSingleContextMessageFactory<Setting<?>> getDefaultInfoMessagePart() {
        return infoDefaultMessagePart;
    }

    public SDCSingleContextMessageFactory<Integer> getNoSuchPageMessage() {
        return noSuchPageMessage;
    }

    public SDCVoidContextMessageFactory getDefaultsNotShown() {
        return defaultsNotShown;
    }

    public SDCVoidContextMessageFactory getAllDefaults() {
        return allDefaults;
    }

    public SDCSingleContextMessageFactory<MobType> getInfoHeader() {
        return infoHeader;
    }

    public SDCDoubleContextMessageFactory<MobType, Paginator<Setting<?>>> getPagedInfoHeader() {
        return pagedInfoHeader;
    }

    public void reloadConfig() {
        getConfig().saveDefaultConfig(); // TODO - remove when bumping SDC
        super.reloadConfig();
        loadMessages();
    }

    public static String formatDouble(double val) {
        String f1 = String.format("%.1f", val);
        String f2 = String.format("%.2f", val);
        if ((f1 + "0").equals(f2)) {
            return f1;
        }
        return f2;
    }

    public static List<String> sort(Collection<String> names) {
        List<String> list = new ArrayList<>(names);
        list.sort((s1, s2) -> s1.compareTo(s2));
        return list;
    }

}
