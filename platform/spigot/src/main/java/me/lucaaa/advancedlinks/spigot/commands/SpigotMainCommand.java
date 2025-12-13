package me.lucaaa.advancedlinks.spigot.commands;

import me.lucaaa.advancedlinks.common.commands.MainCommand;
import me.lucaaa.advancedlinks.spigot.ISpigotAdvancedLinks;
import org.bukkit.ServerLinks;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

@SuppressWarnings("UnstableApiUsage")
public class SpigotMainCommand implements TabExecutor {
    private final ISpigotAdvancedLinks plugin;
    private final MainCommand mainCommand;

    public SpigotMainCommand(ISpigotAdvancedLinks plugin) {
        this.plugin = plugin;
        this.mainCommand = new MainCommand(plugin, ServerLinks.Type.class);

        Objects.requireNonNull(((JavaPlugin) plugin).getCommand("al")).setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return mainCommand.onCommand(plugin.getPlatformManager().getMessageReceiver(sender), args);
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return mainCommand.onTabComplete(plugin.getPlatformManager().getMessageReceiver(sender), args);
    }
}