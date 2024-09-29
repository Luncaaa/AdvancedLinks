package me.lucaaa.advancedlinks.commands.subCommands;

import me.lucaaa.advancedlinks.AdvancedLinks;
import org.bukkit.command.CommandSender;

public class ReloadSubCommand extends SubCommandsFormat {
    public ReloadSubCommand(AdvancedLinks plugin) {
        super(plugin);
    }

    @Override
    public String name() {
        return "reload";
    }

    @Override
    public String description() {
        return "Reloads the plugin's configuration files.";
    }

    @Override
    public String usage() {
        return "/al reload";
    }

    @Override
    public int minArguments() {
        return 0;
    }

    @Override
    public String neededPermission() {
        return "al.reload";
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        plugin.reloadConfigs();
        sender.sendMessage(plugin.getMessagesManager().getColoredMessage("&aThe configuration file has been reloaded successfully.", true));
    }
}