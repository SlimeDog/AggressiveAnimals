package dev.ratas.aggressiveanimals.commands.sub;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

import dev.ratas.aggressiveanimals.AggressiveAnimals;
import dev.ratas.aggressiveanimals.commands.SubCommand;
import dev.ratas.aggressiveanimals.config.ConfigLoadIssueResolver;
import dev.ratas.aggressiveanimals.config.messaging.Messages;
import dev.ratas.slimedogcore.api.messaging.SDCMessage;
import dev.ratas.slimedogcore.api.messaging.context.SDCVoidContext;

public class ReloadSub extends SubCommand {
    private static final String NAME = "reload";
    private static final String USAGE = "/aggro reload";
    private static final String PERMS = "aggressiveanimals.reload";
    private final AggressiveAnimals plugin;
    private final Messages messages;

    public ReloadSub(AggressiveAnimals plugin, Messages messages) {
        super(NAME, USAGE, PERMS);
        this.plugin = plugin;
        this.messages = messages;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        ConfigLoadIssueResolver issues = plugin.reload();
        SDCMessage<SDCVoidContext> msg = (!issues.hasIssues() ? messages.getReloadMessage()
                : messages.getReloadFailedMessage()).getMessage();
        msg.sendTo(sender);
        return true;
    }

}
