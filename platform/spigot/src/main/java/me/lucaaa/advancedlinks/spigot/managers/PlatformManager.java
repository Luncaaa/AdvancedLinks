package me.lucaaa.advancedlinks.spigot.managers;

import me.lucaaa.advancedlinks.common.data.LinkReceiver;
import me.lucaaa.advancedlinks.common.data.MessageReceiver;
import me.lucaaa.advancedlinks.common.tasks.ITasksManager;
import me.lucaaa.advancedlinks.folia.tasks.FoliaTasksManager;
import me.lucaaa.advancedlinks.paper.commands.PaperMainCommand;
import me.lucaaa.advancedlinks.paper.data.PaperLinkReceiver;
import me.lucaaa.advancedlinks.paper.data.PaperMessageReceiver;
import me.lucaaa.advancedlinks.spigot.ISpigotAdvancedLinks;
import me.lucaaa.advancedlinks.spigot.commands.SpigotMainCommand;
import me.lucaaa.advancedlinks.spigot.data.SpigotLinkReceiver;
import me.lucaaa.advancedlinks.spigot.data.SpigotMessageReceiver;
import me.lucaaa.advancedlinks.spigot.tasks.SpigotTasksManager;
import org.bukkit.ServerLinks;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

@SuppressWarnings("UnstableApiUsage")
public class PlatformManager {
    private final ISpigotAdvancedLinks plugin;
    private final Platform platform;

    private final ITasksManager tasksManager;

    public PlatformManager(ISpigotAdvancedLinks plugin) {
        this.plugin = plugin;

        if (hasClass("io.papermc.paper.threadedregions.RegionizedServer")) {
            plugin.log(Level.INFO, "Detected platform: Folia. Enabling support...");
            tasksManager = new FoliaTasksManager(plugin);
            this.platform = Platform.FOLIA;

        } else if (hasClass("io.papermc.paper.event.player.AsyncChatEvent")) {
            plugin.log(Level.INFO, "Detected platform: Paper. Enabling support...");
            tasksManager = new SpigotTasksManager(plugin);
            this.platform = Platform.PAPER;

        } else {
            plugin.log(Level.INFO, "Detected platform: Spigot. Enabling support...");
            tasksManager = new SpigotTasksManager(plugin);
            this.platform = Platform.SPIGOT;
        }
    }

    private boolean hasClass(String clazz) {
        try {
            Class.forName(clazz);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public ITasksManager getTasksManager() {
        return tasksManager;
    }

    public LinkReceiver<ServerLinks.Type> getLinkReceiver(Player player) {
        if (platform == Platform.SPIGOT) {
            return new SpigotLinkReceiver(plugin, player);
        } else {
            return new PaperLinkReceiver(plugin, player);
        }
    }

    public MessageReceiver getMessageReceiver(CommandSender sender) {
        if (platform == Platform.SPIGOT) {
            return new SpigotMessageReceiver(sender);
        } else {
            return new PaperMessageReceiver(sender);
        }
    }

    public void registerMainCommand() {
        String[] versionParts = plugin.getServer().getBukkitVersion().split("-")[0].split("\\.");
        int major = Integer.parseInt(versionParts[1]);
        int minor = (versionParts.length > 2) ? Integer.parseInt(versionParts[2]) : 0;

        if (platform == Platform.SPIGOT || (major <= 21 && minor < 4)) {
            new SpigotMainCommand(plugin);
        } else {
            new PaperMainCommand((JavaPlugin) plugin, sender -> plugin.getPlatformManager().getMessageReceiver(sender));
        }
    }

    private enum Platform {
        SPIGOT,
        PAPER,
        FOLIA
    }
}