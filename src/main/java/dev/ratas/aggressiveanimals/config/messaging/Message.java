package dev.ratas.aggressiveanimals.config.messaging;

import org.bukkit.command.CommandSender;

public class Message {
    private final MessagesBase config;
    private final String path;
    private final String def;
    
    public Message(MessagesBase config, String path, String def) {
        this.config = config;
        this.path = path;
        this.def = def;
    }

    public String getRaw() {
        return config.getConfig().getString(path, def);
    }

    public void sendTo(CommandSender sender) {
        sendTo(sender, null);
    }

    public void sendTo(CommandSender sender, Context context) {
        String msg = getRaw();
        if (context != null) {
            msg = context.fill(msg);
        }
        sender.sendMessage(msg);
    }
    
}
