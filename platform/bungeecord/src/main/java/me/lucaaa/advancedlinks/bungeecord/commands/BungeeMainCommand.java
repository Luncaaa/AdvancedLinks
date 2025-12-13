package me.lucaaa.advancedlinks.bungeecord.commands;

import me.lucaaa.advancedlinks.bungeecord.IBungeeAdvancedLinks;
import me.lucaaa.advancedlinks.common.commands.MainCommand;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ServerLink;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.TabExecutor;

public class BungeeMainCommand extends Command implements TabExecutor {
    private final IBungeeAdvancedLinks plugin;
    private final MainCommand mainCommand;

    public BungeeMainCommand(IBungeeAdvancedLinks plugin) {
        super("al");
        this.plugin = plugin;
        this.mainCommand = new MainCommand(plugin, ServerLink.LinkType.class);

        plugin.getServer().getPluginManager().registerCommand((Plugin) plugin, this);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        mainCommand.onCommand(plugin.getMessageReceiver(sender), args);
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        return mainCommand.onTabComplete(plugin.getMessageReceiver(sender), args);
    }
}