package dev.ratas.aggressiveanimals.commands;

import dev.ratas.aggressiveanimals.AggressiveAnimals;
import dev.ratas.aggressiveanimals.commands.sub.ListSub;
import dev.ratas.aggressiveanimals.commands.sub.ReloadSub;
import dev.ratas.aggressiveanimals.config.messaging.Messages;

public class AggressiveAnimalsCommand extends ParentCommand {

    public AggressiveAnimalsCommand(AggressiveAnimals plugin, Messages messages) {
        addSubCommand(new ReloadSub(plugin, messages));
        addSubCommand(new ListSub(plugin.getAggressivityManager().getMobTypeManager(), messages));
    }

}
