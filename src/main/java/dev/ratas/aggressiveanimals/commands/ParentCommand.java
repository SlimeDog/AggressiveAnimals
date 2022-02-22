package dev.ratas.aggressiveanimals.commands;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.util.StringUtil;

/**
 * ParentCommand
 */
public abstract class ParentCommand implements TabExecutor {
    private final Map<String, SubCommand> subCommands = new LinkedHashMap<>();

    protected void addSubCommand(SubCommand subCommand) {
        addSubCommand(subCommand.getName(), subCommand);
    }

    protected void addSubCommand(String name, SubCommand subCommand) {
        subCommands.put(name.toLowerCase(), subCommand);
    }

    protected boolean removeSubCommand(String name) {
        return subCommands.remove(name) != null;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 1) {
            for (Entry<String, SubCommand> entry : subCommands.entrySet()) {
                if (entry.getValue().hasPermission(sender)) {
                    list.add(entry.getKey());
                }
            }
            return StringUtil.copyPartialMatches(args[0], list, new ArrayList<>());
        } else {
            SubCommand sub = subCommands.get(args[0]);
            if (sub == null || !sub.hasPermission(sender)) {
                return list;
            } else {
                return sub.onTabComplete(sender, command, alias, args);
            }
        }
    }

    String getUsage(CommandSender sender) {
        List<String> msgs = new ArrayList<>();
        for (SubCommand cmd : subCommands.values()) {
            if (cmd.hasPermission(sender)) {
                for (String part : cmd.getUsage(sender, new String[] {}).split("\n")) {
                    msgs.add(part);
                }
            }
        }
        return String.join("\n", msgs);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            return noArgs(sender);
        }
        SubCommand cmd = subCommands.get(args[0]);
        if (cmd == null || !cmd.hasPermission(sender)) {
            showUsage(sender);
            return true;
        }

        if (!cmd.onCommand(sender, command, label, args)) {
            sender.sendMessage(cmd.getUsage(sender, args));
        }
        return true;
    }

    private void showUsage(CommandSender sender) {
        sender.sendMessage(getUsage(sender));
    }

    protected boolean noArgs(CommandSender sender) { // can be overwritten
        showUsage(sender);
        return true;
    }

}
