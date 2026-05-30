package me.lucaaa.advancedlinks.v26_1.mod_common.data;

import me.lucaaa.advancedlinks.common.data.MessageReceiver;
import me.lucaaa.advancedlinks.common.data.Parsers;
import me.lucaaa.advancedlinks.v26_1.mod_common.ModAdvancedLinks;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.permissions.Permissions;

public class ModMessageReceiver implements MessageReceiver {
    protected final ModAdvancedLinks plugin;
    protected final CommandSourceStack sender;

    public ModMessageReceiver(ModAdvancedLinks plugin, CommandSourceStack sender) {
        this.plugin = plugin;
        this.sender = sender;
    }

    @Override
    public void sendMessage(String message) {
        sender.sendSystemMessage(plugin.asNative(Parsers.parseMessage(message)));
    }

    @Override
    public boolean hasPermission(String permission) {
        return sender.permissions().hasPermission(Permissions.COMMANDS_OWNER);
    }
}