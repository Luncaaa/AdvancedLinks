package me.lucaaa.advancedlinks.mod_common.data;

import me.lucaaa.advancedlinks.common.data.MessageReceiver;
import me.lucaaa.advancedlinks.common.data.Parsers;
import me.lucaaa.advancedlinks.versions.v1_21.OldPermissionChecker;
import me.lucaaa.advancedlinks.versions.v1_21_11.NewPermissionChecker;
import me.lucaaa.advancedlinks.mod_common.ModAdvancedLinks;
import net.minecraft.commands.CommandSourceStack;

public class ModMessageReceiver implements MessageReceiver {
    private final ModAdvancedLinks plugin;
    private final CommandSourceStack sender;

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
            return NewPermissionChecker.hasPermission(sender);
        } else {
            return OldPermissionChecker.hasPermission(sender);
        }
    }
}