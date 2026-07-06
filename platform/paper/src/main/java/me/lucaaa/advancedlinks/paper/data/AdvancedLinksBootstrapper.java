package me.lucaaa.advancedlinks.paper.data;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.event.RegistryEvents;
import io.papermc.paper.registry.keys.DialogKeys;
import me.lucaaa.advancedlinks.common.data.Parsers;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jspecify.annotations.NonNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

@SuppressWarnings({"unused", "UnstableApiUsage"})
public class AdvancedLinksBootstrapper implements PluginBootstrap {
    @Override
    public void bootstrap(@NonNull BootstrapContext context) {
        File file = new File(context.getDataDirectory().toAbsolutePath() + File.separator + "config.yml");

        if (!file.exists()) {
            //noinspection ResultOfMethodCallIgnored
            file.getParentFile().mkdirs();
            try (InputStream in = getClass().getClassLoader().getResourceAsStream("config.yml")) {
                FileOutputStream outputStream = new FileOutputStream(file);
                if (in != null) {
                    in.transferTo(outputStream);
                } else {
                    context.getLogger().error("Couldn't find config.yml file in the JAR!");
                }
            } catch (IOException e) {
                context.getLogger().error("Failed to create configuration file: {}", file.getName(), e);
            }
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        String title = config.getString("title");
        String external = config.getString("pauseText");

        if (title == null) {
            context.getLogger().warn("Missing \"title\" setting in config: custom Server Links screen title and pause screen button feature will be disabled.");
            return;
        }

        DialogBase.Builder builder = DialogBase.builder(Parsers.parseMessage(title));

        if (external != null) {
            builder.externalTitle(Parsers.parseMessage(external));
        } else {
            context.getLogger().warn("Missing \"pauseText\" setting in config: Server Links pause screen button won't be changed.");
        }

        try {
            context.getLifecycleManager().registerEventHandler(
                    RegistryEvents.DIALOG.entryAdd().newHandler(event -> {
                        if (!event.key().equals(DialogKeys.SERVER_LINKS)) return;

                        event.builder().base(builder.build());
                    })
            );
        } catch (NoSuchFieldError | Exception e) {
            context.getLogger().warn("Server is running a version which doesn't support Dialogs (1.21.7 feature) - Custom Server Links button feature won't work.");
        }
    }
}