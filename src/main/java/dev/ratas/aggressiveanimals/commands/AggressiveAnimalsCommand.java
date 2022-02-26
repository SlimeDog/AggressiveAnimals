package dev.ratas.aggressiveanimals.commands;

import dev.ratas.aggressiveanimals.AggressiveAnimals;
import dev.ratas.aggressiveanimals.commands.sub.ListSub;
import dev.ratas.aggressiveanimals.commands.sub.ReloadSub;
import dev.ratas.aggressiveanimals.config.messaging.Messages;
import dev.ratas.slimedogcore.impl.commands.BukkitFacingParentCommand;

public class AggressiveAnimalsCommand extends BukkitFacingParentCommand {

    public AggressiveAnimalsCommand(AggressiveAnimals plugin, Messages messages) {
        addSubCommand(new ReloadSub(plugin, messages));
        addSubCommand(new ListSub(plugin.getAggressivityManager().getMobTypeManager(), messages));
    }

}
