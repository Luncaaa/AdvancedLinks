package me.lucaaa.advancedlinks.managers;

import me.lucaaa.advancedlinks.AdvancedLinks;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;
import java.util.function.Consumer;

public class UpdateManager {
    private final AdvancedLinks plugin;
    private final int RESOURCE_ID = 117605;

    public UpdateManager(AdvancedLinks plugin) {
        this.plugin = plugin;
    }

    public void getVersion(final Consumer<String> consumer) {
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            try {
                InputStream resourcePage = new URI("https://api.spigotmc.org/legacy/update.php?resource=" + this.RESOURCE_ID + "/~").toURL().openStream();
                Scanner scanner = new Scanner(resourcePage);
                if (scanner.hasNext()) {
                    consumer.accept(scanner.next());
                }
            } catch (IOException | URISyntaxException e) {
                plugin.getLogger().info("Unable to check for updates: " + e.getMessage());
            }
        });
    }
}