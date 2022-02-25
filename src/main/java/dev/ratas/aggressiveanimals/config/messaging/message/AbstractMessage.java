package dev.ratas.aggressiveanimals.config.messaging.message;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import dev.ratas.aggressiveanimals.config.messaging.context.Context;

public abstract class AbstractMessage<T extends Context> implements Message<T> {
    protected final String raw;
    protected final T context;

    protected AbstractMessage(String raw, T context) {
        this.raw = raw;
        this.context = context;
    }

    public String getRaw() {
        return raw;
    }

    @Override
    public void sendTo(CommandSender sender) {
        sender.sendMessage(color(context.fill(raw)));
    }

    @Override
    public T context() {
        return context;
    }

    protected String color(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

}
