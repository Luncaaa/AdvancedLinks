package me.lucaaa.advancedlinks.commands.subCommands;

import me.lucaaa.advancedlinks.AdvancedLinks;
import org.bukkit.command.CommandSender;

import java.util.Map;

public class HelpSubCommand extends Subcommand {
    private final Map<String, Subcommand> subCommands;

    public HelpSubCommand(AdvancedLinks plugin, Map<String, Subcommand> subCommands) {
        super(plugin);
        this.subCommands = subCommands;
    }

    @Override
    public String name() {
        return "help";
    }

    @Override
    public String description() {
        return "Information about the commands the plugin has.";
    }

    @Override
    public String usage() {
        return "/al help";
    }

    @Override
    public int minArguments() {
        return 0;
    }

    @Override
    public String neededPermission() {
        return null;
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        sender.sendMessage(plugin.getMessagesManager().getColoredMessage("&c---------[ AdvancedLinks help menu ]---------", false));

        sender.sendMessage(plugin.getMessagesManager().getColoredMessage("&cCommands: &7&o([] - mandatory args, <> - optional args)", false));
        for (Subcommand value : subCommands.values()) {
            if (value.neededPermission() == null || sender.hasPermission(value.neededPermission())) {
                sender.sendMessage(plugin.getMessagesManager().getColoredMessage(" &7- &6" + value.usage() + "&7: &e" + value.description(), false));
            }
        }
    }
}
