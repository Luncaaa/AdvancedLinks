package me.lucaaa.advancedlinks.mod_common;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.serialization.JsonOps;
import me.lucaaa.advancedlinks.common.AdvancedLinks;
import me.lucaaa.advancedlinks.common.commands.MainCommand;
import me.lucaaa.advancedlinks.common.managers.ConfigManager;
import me.lucaaa.advancedlinks.common.managers.MessagesManager;
import me.lucaaa.advancedlinks.common.managers.UpdateManager;
import me.lucaaa.advancedlinks.common.tasks.ITasksManager;
import me.lucaaa.advancedlinks.mod_common.data.ModLinkReceiver;
import me.lucaaa.advancedlinks.mod_common.data.ModMessageReceiver;
import me.lucaaa.advancedlinks.mod_common.managers.ModConfigManager;
import me.lucaaa.advancedlinks.mod_common.managers.ModLinksManager;
import me.lucaaa.advancedlinks.mod_common.tasks.ModTaskManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerLinks;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.List;
import java.util.logging.Level;

public abstract class ModAdvancedLinks implements AdvancedLinks<ServerLinks.UntrustedEntry, ServerLinks.KnownLinkType> {
    public static final String MOD_ID = "advancedlinks";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    private static final GsonComponentSerializer gsonSerializer = GsonComponentSerializer.gson();

    // Config files.
    private ConfigManager mainConfig;

    // Managers.
    private ModTaskManager tasksManager;
    private MessagesManager messagesManager;
    private ModLinksManager linksManager;

    // Others.
    private MinecraftServer server;
    private Path configDir;

    public void onInitialize(MinecraftServer server, Path configDir, String version) {
        this.server = server;
        this.configDir = configDir;

        // Set up files and managers.
        reloadConfigs();

        CommandSourceStack console = server.createCommandSourceStack();

        // Look for updates.
        if (mainConfig.getOrDefault("updateChecker", true)) {
            UpdateManager updateManager = new UpdateManager(this);
            updateManager.getVersion(v -> updateManager.sendStatus(new ModMessageReceiver(this, console), v, version));
        }

        messagesManager.sendColoredMessage(new ModMessageReceiver(this, console), "&aThe plugin has been successfully enabled! &7Version: " + version, true);
    }

    public void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
        MainCommand mainCommand = new MainCommand(this, ServerLinks.KnownLinkType.class);
        dispatcher.register(Commands.literal("al")
                .then(Commands.argument("arguments", StringArgumentType.greedyString())
                        .suggests(((cmdContext, builder) -> {
                            String remaining = cmdContext.getInput().substring(builder.getStart());
                            String[] args = remaining.split(" ", -1);

                            List<String> suggestions = mainCommand.onTabComplete(new ModMessageReceiver(this, cmdContext.getSource()), args);

                            if (suggestions != null) {
                                int lastSpaceIndex = remaining.lastIndexOf(' ');
                                int startOfCurrentWord = builder.getStart() + (lastSpaceIndex == -1 ? 0 : lastSpaceIndex + 1);

                                SuggestionsBuilder subBuilder = builder.createOffset(startOfCurrentWord);

                                for (String suggestion : suggestions) {
                                    subBuilder.suggest(suggestion);
                                }
                                return subBuilder.buildFuture();
                            }

                            return builder.buildFuture();
                        }))
                        .executes((cmdContext) -> {
                            String fullArgs = StringArgumentType.getString(cmdContext, "arguments");
                            String[] args = fullArgs.split(" ");
                            try {
                                return mainCommand.onCommand(new ModMessageReceiver(this, cmdContext.getSource()), args) ? 1 : 0;
                            } catch (Exception e) {
                                logError(Level.WARNING, "Error while running command \"al\": ", e);
                                return 0;
                            }
                        })
                )
                .executes(cmdContext -> {
                    try {
                        return mainCommand.onCommand(new ModMessageReceiver(this, cmdContext.getSource()), new String[0]) ? 1 : 0;
                    } catch (Exception e) {
                        logError(Level.WARNING, "Error while running command \"al\": ", e);
                        return 0;
                    }
                })
        );
    }

    public void onPlayerJoin(ServerPlayer player) {
        linksManager.sendLinks(getLinkReceiver(player));
    }

    public void onStop() {
        if (tasksManager != null) tasksManager.shutdown();
        if (linksManager != null) linksManager.shutdown();
    }

    @Override
    public void reloadConfigs() {
        log(Level.INFO, "Detected platform: " + getPlatformName() + ". Enabling support...");
        if (!supportsPapi()) {
            log(Level.WARNING, "Placeholders other than \"%player_name%\" are not supported!");
            log(Level.WARNING, "If you're using Fabric, you can use Text Placeholder API.");
        }

        // Config file.
        mainConfig = new ModConfigManager(this, configDir.resolve("advancedlinks.json").toFile());

        // Managers
        if (linksManager != null) linksManager.shutdown();
        if (tasksManager != null) tasksManager.shutdown();

        tasksManager = new ModTaskManager();
        messagesManager = new MessagesManager(mainConfig.getOrDefault("prefix", "&7[&6AL&7]"));
        linksManager = new ModLinksManager(this, linksManager != null);
    }

    @Override
    public ConfigManager getConfigManager() {
        return mainConfig;
    }

    @Override
    public MessagesManager getMessagesManager() {
        return messagesManager;
    }

    @Override
    public ITasksManager getTasksManager() {
        return tasksManager;
    }

    @Override
    public ModLinksManager getLinksManager() {
        return linksManager;
    }

    @Override
    public void log(Level level, String message) {
        if (level == Level.INFO) {
            LOGGER.info(message);
        } else if (level == Level.WARNING) {
            LOGGER.warn(message);
        } else if (level == Level.SEVERE) {
            LOGGER.error(message);
        }
    }

    @Override
    public void logError(Level level, String message, Throwable error) {
        if (level == Level.INFO) {
            LOGGER.info(message, error);
        } else if (level == Level.WARNING) {
            LOGGER.warn(message, error);
        } else if (level == Level.SEVERE) {
            LOGGER.error(message, error);
        }
    }

    public MinecraftServer getServer() {
        return server;
    }

    // TODO:
    // If, in future versions, the JSON structure of components changes and, therefore, this breaks accross versions,
    // The solution is to create multiple modules (a common one with all fabric code and per-version modules which parse components)
    // Then, make each module depend on a different version of adventure-platform-fabric and have the user provide the jar
    // Said Jar can be downloaded from: https://modrinth.com/mod/adventure-platform-mod
    public net.minecraft.network.chat.Component asNative(Component component) {
        return ComponentSerialization.CODEC.parse(JsonOps.INSTANCE, gsonSerializer.serializeToTree(component)).getOrThrow();
    }

    public Component asAdventure(net.minecraft.network.chat.Component component) {
        return gsonSerializer.deserializeFromTree(ComponentSerialization.CODEC.encodeStart(JsonOps.INSTANCE, component).getOrThrow());
    }

    public abstract ModLinkReceiver getLinkReceiver(ServerPlayer player);

    public abstract String getPlatformName();
}