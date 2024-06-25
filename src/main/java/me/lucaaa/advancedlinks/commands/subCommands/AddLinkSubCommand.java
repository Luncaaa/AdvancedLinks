package me.lucaaa.advancedlinks.commands.subCommands;

import me.lucaaa.advancedlinks.AdvancedLinks;
import org.bukkit.ServerLinks;
import org.bukkit.command.CommandSender;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;

@SuppressWarnings("UnstableApiUsage")
public class AddLinkSubCommand extends SubCommandsFormat {
    public AddLinkSubCommand(AdvancedLinks plugin) {
        super(plugin);
        this.name = "add";
        this.description = "Adds a new server link.";
        this.usage = "/al add [name] [url] [type / display name]";
        this.minArguments = 3;
        this.executableByConsole = true;
        this.neededPermission = "al.add";
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

        URI url;
        try {
            url = new URI(link);
        } catch (URISyntaxException e) {
            sender.sendMessage(plugin.getMessagesManager().getColoredMessage("&cThe link you provided is invalid! Make sure it starts with &bhttps:// &cor &bhttp://", true));
            return;
        }

        boolean success;
        try {
            ServerLinks.Type type = ServerLinks.Type.valueOf(args[3].toUpperCase());
            success = plugin.getLinksManager().addLink(name, type, url);
        } catch (IllegalArgumentException e) {
            String displayName = String.join(" ", Arrays.copyOfRange(args, 3, args.length));
            success = plugin.getLinksManager().addLink(name, displayName, url);
        }

        if (success) {
            sender.sendMessage(plugin.getMessagesManager().getColoredMessage("&aThe link &e" + name + " &ahas been successfully created!", true));
        } else {
            sender.sendMessage(plugin.getMessagesManager().getColoredMessage("&cThe link &b" + name + " &calready exists!", true));
        }
    }
}