package me.lucaaa.advancedlinks.velocity.commands;

import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.util.ServerLink;
import me.lucaaa.advancedlinks.common.commands.MainCommand;
import me.lucaaa.advancedlinks.velocity.IVelocityAdvancedLinks;

import java.util.List;

public class VelocityMainCommand implements SimpleCommand {
    private final IVelocityAdvancedLinks plugin;
    private final MainCommand mainCommand;

    public VelocityMainCommand(IVelocityAdvancedLinks plugin) {
        this.plugin = plugin;
        this.mainCommand = new MainCommand(plugin, ServerLink.Type.class);

        CommandManager commandManager = plugin.getServer().getCommandManager();

        CommandMeta commandMeta = commandManager.metaBuilder("al")
                .plugin(plugin)
                .build();

        commandManager.register(commandMeta, this);
    }

    @Override
    public void execute(Invocation invocation) {
        mainCommand.onCommand(plugin.getMessageReceiver(invocation.source()), invocation.arguments());
    }

    @Override
    public List<String> suggest(Invocation invocation) {
        return mainCommand.onTabComplete(plugin.getMessageReceiver(invocation.source()), invocation.arguments());
    }
}