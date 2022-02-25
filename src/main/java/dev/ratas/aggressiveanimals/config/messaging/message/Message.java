package dev.ratas.aggressiveanimals.config.messaging.message;

import org.bukkit.command.CommandSender;

import dev.ratas.aggressiveanimals.config.messaging.context.Context;

public interface Message<T extends Context> {

    String getRaw();

    void sendTo(CommandSender sender);

    T context();

}
