package me.lucaaa.advancedlinks.neoforge;

import me.lucaaa.advancedlinks.mod_common.ModAdvancedLinks;
import me.lucaaa.advancedlinks.mod_common.data.ModMessageReceiver;
import net.minecraft.commands.CommandSourceStack;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.logging.Level;

public class NeoForgeMessageReceiver extends ModMessageReceiver {
    public NeoForgeMessageReceiver(ModAdvancedLinks plugin, CommandSourceStack sender) {
        super(plugin, sender);
    }

    @Override
    protected boolean hasPermissionsNew() {
        try {
            // permissions() method from the sender's class, which returns a PermissionSet instance.
            Method permissionsMethod = sender.getClass().getMethod("permissions");
            Object permissionSet = permissionsMethod.invoke(sender);

            // Permissions class and its field COMMANDS_OWNER.
            Class<?> permissionsClass = Class.forName("net.minecraft.server.permissions.Permissions");
            Field commandsOwnerField = permissionsClass.getField("COMMANDS_OWNER");
            Object commandsOwnerPermission = commandsOwnerField.get(null);

            // hasPermission method (which has Permission as a param) from the PermissionSet class.
            Class<?> permissionSetClass = Class.forName("net.minecraft.server.permissions.PermissionSet");
            Class<?> permissionClass = Class.forName("net.minecraft.server.permissions.Permission");
            Method hasPermissionMethod = permissionSetClass.getMethod("hasPermission", permissionClass);

            // Invoke the hasPermission method from the PermissionSet class passing COMMANDS_OWNER.
            return (boolean) hasPermissionMethod.invoke(permissionSet, commandsOwnerPermission);
        } catch (ReflectiveOperationException e) {
            plugin.logError(Level.SEVERE, "Couldn't get a user's permission level!", e);
            return false;
        }
    }
}