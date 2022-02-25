package dev.ratas.aggressiveanimals.commands.sub;

import java.util.Collections;
import java.util.List;

import org.bukkit.command.CommandSender;

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
    private static final String USAGE = "/aggro list";
    private static final String PERMS = "aggressiveanimals.list";
    private final MobTypeManager manager;
    private final Messages messages;

    public ListSub(MobTypeManager manager, Messages messages) {
        super(NAME, USAGE, PERMS);
        this.manager = manager;
        this.messages = messages;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        messages.getListHeaderMessage().getMessage(Context.NULL).sendTo(sender);
        MessageFactory.DoubleFactory<MobType, Boolean> lmf = messages.getListItemMessage();
        for (MobTypeSettings settings : manager.getUsedSettings()) {
            DoubleContextFactory<MobType, Boolean> cf = lmf.getContextFactory();
            lmf.getMessage(cf.getContext(settings.entityType(), settings.enabled())).sendTo(sender);
        }
        return true;
    }

}
