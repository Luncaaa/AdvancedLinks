package me.lucaaa.advancedlinks.commands.subCommands;

import me.lucaaa.advancedlinks.AdvancedLinks;
import org.bukkit.command.CommandSender;

public class ReloadSubCommand extends SubCommandsFormat {
    public ReloadSubCommand(AdvancedLinks plugin) {
        super(plugin);
        this.name = "reload";
        this.description = "Reloads the plugin's configuration files.";
        this.usage = "/al reload";
        this.minArguments = 0;
        this.executableByConsole = true;
        this.neededPermission = "al.reload";
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        plugin.reloadConfigs();
        sender.sendMessage(plugin.getMessagesManager().getColoredMessage("&aThe configuration file has been reloaded successfully.", true));
    }
}