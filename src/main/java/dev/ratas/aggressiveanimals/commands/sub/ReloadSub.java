package dev.ratas.aggressiveanimals.commands.sub;

import java.util.ArrayList;
import java.util.List;

import dev.ratas.aggressiveanimals.AggressiveAnimals;
import dev.ratas.aggressiveanimals.config.ConfigLoadIssueResolver;
import dev.ratas.aggressiveanimals.config.messaging.Messages;
import dev.ratas.slimedogcore.api.messaging.SDCMessage;
import dev.ratas.slimedogcore.api.messaging.context.SDCVoidContext;
import dev.ratas.slimedogcore.api.messaging.recipient.SDCRecipient;
import dev.ratas.slimedogcore.impl.commands.AbstractSubCommand;

public class ReloadSub extends AbstractSubCommand {
    private static final String NAME = "reload";
    private static final String USAGE = "/aggro reload";
    private static final String PERMS = "aggressiveanimals.reload";
    private final AggressiveAnimals plugin;
    private final Messages messages;

    public ReloadSub(AggressiveAnimals plugin, Messages messages) {
        super(NAME, PERMS, USAGE);
        this.plugin = plugin;
        this.messages = messages;
    }

    @Override
    public List<String> onTabComplete(SDCRecipient sender, String[] args) {
        return new ArrayList<>();
    }

    @Override
    public boolean onCommand(SDCRecipient sender, String[] args, List<String> opts) {
        ConfigLoadIssueResolver issues = plugin.reload();
        SDCMessage<SDCVoidContext> msg = (!issues.hasIssues() ? messages.getReloadMessage()
                : messages.getReloadFailedMessage()).getMessage();
        msg.sendTo(sender);
        return true;
    }

}
