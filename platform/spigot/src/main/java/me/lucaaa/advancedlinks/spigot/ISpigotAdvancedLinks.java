package me.lucaaa.advancedlinks.spigot;

import me.lucaaa.advancedlinks.common.AdvancedLinks;
import me.lucaaa.advancedlinks.spigot.managers.PlatformManager;
import org.bukkit.plugin.Plugin;

public interface ISpigotAdvancedLinks extends AdvancedLinks, Plugin {
    PlatformManager getPlatformManager();
}