package me.lucaaa.advancedlinks.paper.commands;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.lucaaa.advancedlinks.common.AdvancedLinks;
import me.lucaaa.advancedlinks.common.commands.MainCommand;
import me.lucaaa.advancedlinks.common.data.MessageReceiver;
import org.bukkit.ServerLinks;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.function.Function;

@SuppressWarnings("UnstableApiUsage")
public class PaperMainCommand {
    private final MainCommand mainCommand;

    public PaperMainCommand(AdvancedLinks<ServerLinks.ServerLink, ServerLinks.Type> plugin, Function<CommandSender, MessageReceiver> getReceiver) {
        this.mainCommand = new MainCommand(plugin, ServerLinks.Type.class);

        ((JavaPlugin) plugin).registerCommand(
                "al",
                "Main command.",
                new BasicCommand() {
                    @Override
                    public void execute(@NonNull CommandSourceStack commandSourceStack, String @NonNull [] args) {
                        mainCommand.onCommand(getReceiver.apply(commandSourceStack.getSender()), args);
                    }

                    @NonNull
                    @Override
                    public Collection<String> suggest(@NonNull CommandSourceStack commandSourceStack, String @NonNull [] args) {
                        return mainCommand.onTabComplete(getReceiver.apply(commandSourceStack.getSender()), args);
                    }
                }
        );
    }
}