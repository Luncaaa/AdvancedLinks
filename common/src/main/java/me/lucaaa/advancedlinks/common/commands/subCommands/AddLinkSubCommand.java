package me.lucaaa.advancedlinks.common.commands.subCommands;

import me.lucaaa.advancedlinks.common.AdvancedLinks;
import me.lucaaa.advancedlinks.common.data.MessageReceiver;

import java.util.ArrayList;
import java.util.Arrays;

public class AddLinkSubCommand<T, S extends Enum<S>> extends Subcommand {
    private final Class<S> enumClass;

    public AddLinkSubCommand(AdvancedLinks<T, S> plugin, Class<S> enumClass) {
        super(plugin);
        this.enumClass = enumClass;
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
    public ArrayList<String> getTabCompletions(MessageReceiver sender, String[] args) {
        ArrayList<String> completions = new ArrayList<>();
        if (args.length == 3) {
            completions.add("https://");
        } else if (args.length >= 4) {
            for (S type : enumClass.getEnumConstants()) {
                completions.add(type.name());
            }
        }
        return completions;
    }

    @Override
    public void run(MessageReceiver sender, String[] args) {
        String name = args[1];
        String link = args[2];

        if (!link.startsWith("https://") && !link.startsWith("http://")) {
            plugin.getMessagesManager().sendColoredMessage(sender, "&cThe link you provided is invalid! Make sure it starts with &bhttps:// &cor &bhttp://", true);
            return;
        }

        boolean success = plugin.getLinksManager().addLink(name, String.join(" ", Arrays.copyOfRange(args, 3, args.length)), link);
        if (success) {
            plugin.getMessagesManager().sendColoredMessage(sender, "&aThe link &e" + name + " &ahas been successfully created!", true);
        } else {
            plugin.getMessagesManager().sendColoredMessage(sender, "&cThe link &b" + name + " &calready exists!", true);
        }
    }
}