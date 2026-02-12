package me.lucaaa.advancedlinks.spigot;

import me.lucaaa.advancedlinks.common.AdvancedLinks;
import me.lucaaa.advancedlinks.spigot.managers.PlatformManager;
import org.bukkit.ServerLinks;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("UnstableApiUsage")
public abstract class ISpigotAdvancedLinks extends JavaPlugin implements AdvancedLinks<ServerLinks.ServerLink, ServerLinks.Type> {
    public abstract PlatformManager getPlatformManager();
}