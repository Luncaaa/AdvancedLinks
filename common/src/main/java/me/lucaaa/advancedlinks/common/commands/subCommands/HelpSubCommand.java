package me.lucaaa.advancedlinks.common.commands.subCommands;

import me.lucaaa.advancedlinks.common.AdvancedLinks;
import me.lucaaa.advancedlinks.common.data.MessageReceiver;

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
    public void run(MessageReceiver sender, String[] args) {
        plugin.getMessagesManager().sendColoredMessage(sender, "&c---------[ AdvancedLinks help menu ]---------", false);

        plugin.getMessagesManager().sendColoredMessage(sender, "&cCommands: &7&o([] - mandatory args, <> - optional args)", false);
        for (Subcommand value : subCommands.values()) {
            if (value.neededPermission() == null || sender.hasPermission(value.neededPermission())) {
                plugin.getMessagesManager().sendColoredMessage(sender, " &7- &6" + value.usage() + "&7: &e" + value.description(), false);
            }
        }
    }
}
