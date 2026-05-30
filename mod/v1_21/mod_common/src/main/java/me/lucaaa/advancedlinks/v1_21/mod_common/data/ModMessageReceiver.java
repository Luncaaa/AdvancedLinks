package me.lucaaa.advancedlinks.mod_common.data;

import me.lucaaa.advancedlinks.common.data.MessageReceiver;
import me.lucaaa.advancedlinks.common.data.Parsers;
import me.lucaaa.advancedlinks.mod_common.ModAdvancedLinks;
import net.minecraft.commands.CommandSourceStack;

public abstract class ModMessageReceiver implements MessageReceiver {
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
        if (plugin.getServer().getServerVersion().equals("1.21.11")) {
            // Use reflection: return sender.permissions().hasPermission(Permissions.COMMANDS_OWNER);
            return hasPermissionsNew();
        } else {
            return sender.hasPermission(4);
        }
    }

    protected abstract boolean hasPermissionsNew();
}