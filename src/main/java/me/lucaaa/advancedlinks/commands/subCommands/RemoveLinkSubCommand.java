package me.lucaaa.advancedlinks.commands.subCommands;

import me.lucaaa.advancedlinks.AdvancedLinks;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;

public class RemoveLinkSubCommand extends SubCommandsFormat {
    public RemoveLinkSubCommand(AdvancedLinks plugin) {
        super(plugin);
    }

    @Override
    public String name() {
        return "remove";
    }

    @Override
    public String description() {
        return "Removes an existing server link.";
    }

    @Override
    public String usage() {
        return "/al remove [name]";
    }

    @Override
    public int minArguments() {
        return 1;
    }

    @Override
    public String neededPermission() {
        return "al.remove";
    }

    @Override
    public ArrayList<String> getTabCompletions(CommandSender sender, String[] args) {
        return new ArrayList<>(plugin.getLinksManager().getKeys());
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        String name = args[1];

        boolean success = plugin.getLinksManager().removeLink(name);

        if (success) {
            sender.sendMessage(plugin.getMessagesManager().getColoredMessage("&aThe link &e" + name + " &ahas been successfully removed!", true));
        } else {
            sender.sendMessage(plugin.getMessagesManager().getColoredMessage("&cThe link &b" + name + " &cdoes not exist!", true));
        }
    }
}