package me.lucaaa.advancedlinks.commands.subCommands;

import me.lucaaa.advancedlinks.AdvancedLinks;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;

public class RemoveLinkSubCommand extends SubCommandsFormat {
    public RemoveLinkSubCommand(AdvancedLinks plugin) {
        super(plugin);
        this.name = "remove";
        this.description = "Removes a server link.";
        this.usage = "/al remove [name]";
        this.minArguments = 1;
        this.executableByConsole = true;
        this.neededPermission = "al.remove";
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