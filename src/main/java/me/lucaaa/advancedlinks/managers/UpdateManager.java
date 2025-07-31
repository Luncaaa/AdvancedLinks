package me.lucaaa.advancedlinks.managers;

import me.lucaaa.advancedlinks.AdvancedLinks;
import org.bukkit.command.ConsoleCommandSender;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.logging.Level;

public class UpdateManager {
    private final AdvancedLinks plugin;
    private final int RESOURCE_ID = 117605;

    public UpdateManager(AdvancedLinks plugin) {
        this.plugin = plugin;
    }

    public void getVersion(final Consumer<String> consumer) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                InputStream resourcePage = new URI("https://api.spigotmc.org/legacy/update.php?resource=" + RESOURCE_ID + "/~").toURL().openStream();
                try (Scanner scanner = new Scanner(resourcePage)) {
                    if (scanner.hasNext()) {
                        consumer.accept(scanner.next());
                    }
                }
            } catch (IOException | URISyntaxException e) {
                plugin.log(Level.INFO, "Unable to check for updates: " + e.getMessage());
            }
        });
    }

    public static void sendStatus(AdvancedLinks plugin, String spigotVersion, String pluginVersion) {
        ConsoleCommandSender console = plugin.getServer().getConsoleSender();
        MessagesManager messagesManager = plugin.getMessagesManager();
        
        String[] spigotVerDivided = spigotVersion.split("\\.");
        double spigotVerMajor = Double.parseDouble(spigotVerDivided[0] + "." + spigotVerDivided[1]);
        double spigotVerMinor = (spigotVerDivided.length > 2) ? Integer.parseInt(spigotVerDivided[2]) : 0;

        String[] pluginVerDivided = pluginVersion.split("\\.");
        double pluginVerMajor = Double.parseDouble(pluginVerDivided[0] + "." + pluginVerDivided[1]);
        double pluginVerMinor = (pluginVerDivided.length > 2) ? Integer.parseInt(pluginVerDivided[2]) : 0;

        if (spigotVerMajor == pluginVerMajor && spigotVerMinor == pluginVerMinor) {
            console.sendMessage(messagesManager.getColoredMessage("&aThe plugin is up to date! &7(v" + pluginVersion + ")", true));

        } else if (spigotVerMajor > pluginVerMajor || (spigotVerMajor == pluginVerMajor && spigotVerMinor > pluginVerMinor)) {
            console.sendMessage(messagesManager.getColoredMessage("&6There's a new update available on Spigot! &c" + pluginVersion + " &7-> &a" + spigotVersion, true));
            console.sendMessage(messagesManager.getColoredMessage("&6Download it at &7https://www.spigotmc.org/resources/advancedlinks.117605/", true));

        } else {
            console.sendMessage(messagesManager.getColoredMessage("&6Your plugin version is newer than the Spigot version! &a" + pluginVersion + " &7-> &c" + spigotVersion, true));
            console.sendMessage(messagesManager.getColoredMessage("&6There may be bugs and/or untested features!", true));
        }
    }
}