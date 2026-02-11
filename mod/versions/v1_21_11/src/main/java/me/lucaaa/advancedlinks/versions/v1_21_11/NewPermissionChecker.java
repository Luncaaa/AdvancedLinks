package me.lucaaa.advancedlinks.versions.v1_21_11;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.permissions.Permissions;

public class NewPermissionChecker {
    public static boolean hasPermission(CommandSourceStack sender) {
        return sender.permissions().hasPermission(Permissions.COMMANDS_OWNER);
    }
}