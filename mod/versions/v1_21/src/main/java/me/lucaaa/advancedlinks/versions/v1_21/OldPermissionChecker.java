package me.lucaaa.advancedlinks.versions.v1_21;

import net.minecraft.commands.CommandSourceStack;

public class OldPermissionChecker {
    public static boolean hasPermission(CommandSourceStack sender) {
        return sender.hasPermission(4);
    }
}