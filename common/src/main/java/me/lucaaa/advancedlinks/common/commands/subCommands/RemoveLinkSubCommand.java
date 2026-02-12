package me.lucaaa.advancedlinks.common.commands.subCommands;

import me.lucaaa.advancedlinks.common.AdvancedLinks;
import me.lucaaa.advancedlinks.common.data.MessageReceiver;

import java.util.ArrayList;

public class RemoveLinkSubCommand extends Subcommand {
    public RemoveLinkSubCommand(AdvancedLinks<?, ?> plugin) {
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
    public ArrayList<String> getTabCompletions(MessageReceiver sender, String[] args) {
        return new ArrayList<>(plugin.getLinksManager().getKeys());
    }

    @Override
    public void run(MessageReceiver sender, String[] args) {
        String name = args[1];

        boolean success = plugin.getLinksManager().removeLink(name);

        if (success) {
            plugin.getMessagesManager().sendColoredMessage(sender, "&aThe link &e" + name + " &ahas been successfully removed!", true);
        } else {
            plugin.getMessagesManager().sendColoredMessage(sender, "&cThe link &b" + name + " &cdoes not exist!", true);
        }
    }
}