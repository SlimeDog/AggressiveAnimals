package dev.ratas.aggressiveanimals.commands;

import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

public abstract class SubCommand implements TabExecutor {
    private final String name;
    private final String usage;
    private final String perms;

    protected SubCommand(String name, String usage, String perms) {
        this.name = name;
        this.usage = usage;
        this.perms = perms;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return onTabComplete(sender, Arrays.copyOfRange(args, 1, args.length));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return onCommand(sender, Arrays.copyOfRange(args, 1, args.length));
    }

    protected Player getPlayerOrNull(CommandSender sender) {
        if (sender instanceof Player) {
            return (Player) sender;
        } else {
            return null;
        }
    }

    public abstract List<String> onTabComplete(CommandSender sender, String[] args);

    public abstract boolean onCommand(CommandSender sender, String[] args);

    public String getName() {
        return name;
    }

    public boolean hasPermission(CommandSender sender) {
        return sender.hasPermission(perms);
    }

    public String getUsage(CommandSender sender, String[] args) {
        return usage;
    }

}
