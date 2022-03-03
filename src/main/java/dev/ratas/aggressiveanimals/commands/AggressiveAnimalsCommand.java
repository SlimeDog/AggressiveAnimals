package dev.ratas.aggressiveanimals.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import dev.ratas.aggressiveanimals.IAggressiveAnimals;
import dev.ratas.aggressiveanimals.commands.sub.HelpSub;
import dev.ratas.aggressiveanimals.commands.sub.InfoSub;
import dev.ratas.aggressiveanimals.commands.sub.ListSub;
import dev.ratas.aggressiveanimals.commands.sub.ReloadSub;
import dev.ratas.aggressiveanimals.config.messaging.Messages;
import dev.ratas.slimedogcore.api.commands.SDCSubCommand;
import dev.ratas.slimedogcore.api.messaging.recipient.SDCRecipient;
import dev.ratas.slimedogcore.impl.commands.BukkitFacingParentCommand;
import dev.ratas.slimedogcore.impl.wrappers.BukkitAdapter;

public class AggressiveAnimalsCommand extends BukkitFacingParentCommand {

    public AggressiveAnimalsCommand(IAggressiveAnimals plugin, Messages messages) {
        addSubCommand(new ReloadSub(plugin, messages));
        addSubCommand(new ListSub(plugin.getAggressivityManager().getMobTypeManager(), messages));
        addSubCommand(new InfoSub(plugin.getAggressivityManager(), messages));
        addSubCommand(new HelpSub(this));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            SDCRecipient recipient = BukkitAdapter.adapt(sender);
            String usage = getUsage(recipient);
            recipient.sendRawMessage(usage);
            return true;
        }
        return super.onCommand(sender, command, label, args);
    }

    public String getUsage(SDCRecipient recipient) {
        List<String> usages = new ArrayList<>();
        String[] args = new String[] {};
        for (String subName : getApplicableSubCommandNames(recipient)) {
            SDCSubCommand sc = getSubCommand(subName);
            usages.add(sc.getUsage(recipient, args));
        }
        return String.join("\n", usages);
    }

}
