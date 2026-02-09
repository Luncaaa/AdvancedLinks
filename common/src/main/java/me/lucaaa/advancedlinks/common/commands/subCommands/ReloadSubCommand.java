package me.lucaaa.advancedlinks.common.commands.subCommands;

import me.lucaaa.advancedlinks.common.AdvancedLinks;
import me.lucaaa.advancedlinks.common.data.MessageReceiver;

public class ReloadSubCommand extends Subcommand {
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
    public void run(MessageReceiver sender, String[] args) {
        plugin.reloadConfigs();
        plugin.getMessagesManager().sendColoredMessage(sender, "&aThe configuration file has been reloaded successfully.", true);
    }
}