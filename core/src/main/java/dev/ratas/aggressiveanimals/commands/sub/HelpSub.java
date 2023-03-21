package dev.ratas.aggressiveanimals.commands.sub;

import java.util.ArrayList;
import java.util.List;

import dev.ratas.aggressiveanimals.commands.AggressiveAnimalsCommand;
import dev.ratas.slimedogcore.api.commands.SDCCommandOptionSet;
import dev.ratas.slimedogcore.api.messaging.recipient.SDCRecipient;
import dev.ratas.slimedogcore.impl.commands.AbstractSubCommand;

public class HelpSub extends AbstractSubCommand {
    private static final String NAME = "help";
    private static final String PERMS = "aggressiveanimals.use";
    private static final String USAGE = "/aggro help";
    private final AggressiveAnimalsCommand parent;

    public HelpSub(AggressiveAnimalsCommand parent) {
        super(NAME, PERMS, USAGE);
        this.parent = parent;
    }

    @Override
    public List<String> onTabComplete(SDCRecipient sender, String[] args) {
        return new ArrayList<>();
    }

    @Override
    public boolean onOptionedCommand(SDCRecipient sender, String[] args, SDCCommandOptionSet opts) {
        sender.sendRawMessage(parent.getUsage(sender));
        return true;
    }

}
