package me.lucaaa.advancedlinks;

import me.lucaaa.advancedlinks.commands.MainCommand;
import me.lucaaa.advancedlinks.managers.ConfigManager;
import me.lucaaa.advancedlinks.managers.LinksManager;
import me.lucaaa.advancedlinks.managers.MessagesManager;
import me.lucaaa.advancedlinks.managers.UpdateManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Objects;

public class AdvancedLinks extends JavaPlugin {
    // Managers.
    private MessagesManager messagesManager;
    private LinksManager linksManager;

    // Reload the config files.
    public void reloadConfigs() {
        // Creates the config file.
        if (!new File(getDataFolder().getAbsolutePath() + File.separator + "config.yml").exists())
            saveResource("config.yml", false);

        // Config file.
        ConfigManager mainConfig = new ConfigManager(this, "config.yml");

        // Managers
        messagesManager = new MessagesManager(mainConfig);
        if (linksManager != null) linksManager.removeLinks();
        linksManager = new LinksManager(this, mainConfig, linksManager != null);
    }

    @Override
    public void onEnable() {
        // Set up files and managers.
        reloadConfigs();

        // Look for updates.
        new UpdateManager(this).getVersion(v -> {
            String[] spigotVerDivided = v.split("\\.");
            double spigotVerMajor = Double.parseDouble(spigotVerDivided[0] + "." + spigotVerDivided[1]);
            double spigotVerMinor = (spigotVerDivided.length > 2) ? Integer.parseInt(spigotVerDivided[2]) : 0;

            String[] pluginVerDivided = getDescription().getVersion().split("\\.");
            double pluginVerMajor = Double.parseDouble(pluginVerDivided[0] + "." + pluginVerDivided[1]);
            double pluginVerMinor = (pluginVerDivided.length > 2) ? Integer.parseInt(pluginVerDivided[2]) : 0;

            if (spigotVerMajor == pluginVerMajor && spigotVerMinor == pluginVerMinor) {
                Bukkit.getConsoleSender().sendMessage(messagesManager.getColoredMessage("&aThe plugin is up to date! &7(v" + getDescription().getVersion() + ")", true));

            } else if (spigotVerMajor > pluginVerMajor || (spigotVerMajor == pluginVerMajor && spigotVerMinor > pluginVerMinor)) {
                Bukkit.getConsoleSender().sendMessage(messagesManager.getColoredMessage("&6There's a new update available on Spigot! &c" + getDescription().getVersion() + " &7-> &a" + v, true));
                Bukkit.getConsoleSender().sendMessage(messagesManager.getColoredMessage("&6Download it at &7https://www.spigotmc.org/resources/advanceddisplays.110865/", true));

            } else {
                Bukkit.getConsoleSender().sendMessage(messagesManager.getColoredMessage("&6Your plugin version is newer than the Spigot version! &a" + getDescription().getVersion() + " &7-> &c" + v, true));
                Bukkit.getConsoleSender().sendMessage(messagesManager.getColoredMessage("&6There may be bugs and/or untested features!", true));
            }
        });

        // Registers the main command and adds tab completions.
        MainCommand commandHandler = new MainCommand(this);
        Objects.requireNonNull(this.getCommand("al")).setExecutor(commandHandler);
        Objects.requireNonNull(this.getCommand("al")).setTabCompleter(commandHandler);

        Bukkit.getConsoleSender().sendMessage(messagesManager.getColoredMessage("&aThe plugin has been successfully enabled! &7Version: " + this.getDescription().getVersion(), true));
    }

    public MessagesManager getMessagesManager() {
        return this.messagesManager;
    }

    public LinksManager getLinksManager() {
        return this.linksManager;
    }
}