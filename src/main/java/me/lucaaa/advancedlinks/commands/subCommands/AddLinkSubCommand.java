package me.lucaaa.advancedlinks.commands.subCommands;

import me.lucaaa.advancedlinks.AdvancedLinks;
import org.bukkit.ServerLinks;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;

@SuppressWarnings("UnstableApiUsage")
public class AddLinkSubCommand extends SubCommandsFormat {
    public AddLinkSubCommand(AdvancedLinks plugin) {
        super(plugin);
    }

    @Override
    public String name() {
        return "add";
    }

    @Override
    public String description() {
        return "Adds a new server link.";
    }

    @Override
    public String usage() {
        return "/al add [name] [url] [type / display name]";
    }

    @Override
    public int minArguments() {
        return 3;
    }

    @Override
    public String neededPermission() {
        return "al.add";
    }

    @Override
    public ArrayList<String> getTabCompletions(CommandSender sender, String[] args) {
        ArrayList<String> completions = new ArrayList<>();
        if (args.length == 3) {
            completions.add("https://");
        } else if (args.length >= 4) {
            for (ServerLinks.Type type : ServerLinks.Type.values()) {
                completions.add(type.name());
            }
        }
        return completions;
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        String name = args[1];
        String link = args[2];

        if (!link.startsWith("https://") && !link.startsWith("http://")) {
            sender.sendMessage(plugin.getMessagesManager().getColoredMessage("&cThe link you provided is invalid! Make sure it starts with &bhttps:// &cor &bhttp://", true));
            return;
        }

        boolean success = plugin.getLinksManager().addLink(name, String.join(" ", Arrays.copyOfRange(args, 3, args.length)), link);
        if (success) {
            sender.sendMessage(plugin.getMessagesManager().getColoredMessage("&aThe link &e" + name + " &ahas been successfully created!", true));
        } else {
            sender.sendMessage(plugin.getMessagesManager().getColoredMessage("&cThe link &b" + name + " &calready exists!", true));
        }
    }
}