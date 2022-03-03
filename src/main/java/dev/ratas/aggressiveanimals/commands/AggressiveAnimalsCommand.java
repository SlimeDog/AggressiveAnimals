package dev.ratas.aggressiveanimals.commands;

import java.util.List;

import dev.ratas.aggressiveanimals.IAggressiveAnimals;
import dev.ratas.aggressiveanimals.commands.sub.HelpSub;
import dev.ratas.aggressiveanimals.commands.sub.InfoSub;
import dev.ratas.aggressiveanimals.commands.sub.ListSub;
import dev.ratas.aggressiveanimals.commands.sub.ReloadSub;
import dev.ratas.aggressiveanimals.config.messaging.Messages;
import dev.ratas.slimedogcore.api.messaging.recipient.SDCRecipient;
import dev.ratas.slimedogcore.impl.commands.BukkitFacingParentCommand;

public class AggressiveAnimalsCommand extends BukkitFacingParentCommand {

    public AggressiveAnimalsCommand(IAggressiveAnimals plugin, Messages messages) {
        addSubCommand(new ReloadSub(plugin, messages));
        addSubCommand(new ListSub(plugin.getAggressivityManager().getMobTypeManager(), messages));
        addSubCommand(new InfoSub(plugin.getAggressivityManager(), messages));
        addSubCommand(new HelpSub(this));
    }

    @Override
    public boolean onCommand(SDCRecipient sender, String[] args, List<String> opts) {
        if (args.length == 0) {
            sender.sendRawMessage(getUsage(sender));
            return true;
        }
        return super.onCommand(sender, args, opts);
    }

}
