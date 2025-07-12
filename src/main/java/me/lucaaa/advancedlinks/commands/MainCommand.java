package me.lucaaa.advancedlinks.commands;

import me.lucaaa.advancedlinks.AdvancedLinks;
import me.lucaaa.advancedlinks.commands.subCommands.*;
import me.lucaaa.advancedlinks.managers.MessagesManager;
import org.bukkit.command.*;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainCommand implements TabExecutor {
    private final AdvancedLinks plugin;
    private final HashMap<String, Subcommand> subCommands = new HashMap<>();

    public MainCommand(AdvancedLinks plugin) {
        this.plugin = plugin;
        addSubCommand(new ReloadSubCommand(plugin));
        addSubCommand(new AddLinkSubCommand(plugin));
        addSubCommand(new RemoveLinkSubCommand(plugin));
        addSubCommand(new HelpSubCommand(plugin, subCommands));
    }

    public void addSubCommand(Subcommand subCommand) {
        subCommands.put(subCommand.name(), subCommand);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        MessagesManager messagesManager = plugin.getMessagesManager();

        // If there are no arguments, show an error.
        if (args.length == 0) {
            sender.sendMessage(messagesManager.getColoredMessage("&cYou need to enter more arguments to run this command!", true));
            sender.sendMessage(messagesManager.getColoredMessage("&cUse &b/ad help &cto see the list of existing commands.", true));
            return true;
        }

        // If the subcommand does not exist, show an error.
        if (!subCommands.containsKey(args[0])) {
            sender.sendMessage(messagesManager.getColoredMessage("&cThe command " + args[0] + " &cdoes not exist!", true));
            sender.sendMessage(messagesManager.getColoredMessage("&cUse &b/ad help &cto see the list of existing commands.", true));
            return true;
        }

        // If the subcommand exists, get it from the map.
        Subcommand subCommand = subCommands.get(args[0]);

        // If the player who ran the command does not have the needed permissions, show an error.
        if (subCommand.neededPermission() != null && !sender.hasPermission(subCommand.neededPermission())) {
            sender.sendMessage(messagesManager.getColoredMessage("&cYou don't have permission to execute this command!", true));
            return true;
        }

        // If the user entered fewer arguments than the subcommand needs, an error will appear.
        // args.size - 1 because the name of the subcommand is not included in the minArguments
        if (args.length - 1 < subCommand.minArguments()) {
            sender.sendMessage(messagesManager.getColoredMessage("&cYou need to enter more arguments to run this command!", true));
            sender.sendMessage(messagesManager.getColoredMessage("&7Correct usage: &c" + subCommand.usage(), true));
            return true;
        }

        // If the command is valid, run it.
        subCommand.run(sender, args);
        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        ArrayList<String> completions = new ArrayList<>();

        // Tab completions for each subcommand. If the user is going to type the first argument, and it does not need any permission
        // to be executed, complete it. If it needs a permission, check if the user has it and add more completions.
        if (args.length == 1) {
            for (Map.Entry<String, Subcommand> entry : subCommands.entrySet()) {
                if (entry.getValue().neededPermission() == null || sender.hasPermission(entry.getValue().neededPermission())) {
                    completions.add(entry.getKey());
                }
            }
        }

        // Command's second argument.
        if (args.length >= 2 && subCommands.containsKey(args[0])) {
            completions = subCommands.get(args[0]).getTabCompletions(sender, args);
        }

        // Filters the array so only the completions that start with what the user is typing are shown.
        // For example, it can complete "reload", "removeDisplay" and "help". If the user doesn't type anything, all those
        // options will appear. If the user starts typing "r", only "reload" and "removeDisplay" will appear.
        // args[args.size-1] -> To get the argument the user is typing (first, second...)
        return completions.stream().filter(completion -> completion.toLowerCase().contains(args[args.length-1].toLowerCase())).toList();
    }
}

