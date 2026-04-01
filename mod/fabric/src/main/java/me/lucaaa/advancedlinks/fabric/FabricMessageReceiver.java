package me.lucaaa.advancedlinks.fabric;

import me.lucaaa.advancedlinks.mod_common.ModAdvancedLinks;
import me.lucaaa.advancedlinks.mod_common.data.ModMessageReceiver;
import net.minecraft.commands.CommandSourceStack;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.logging.Level;

public class FabricMessageReceiver extends ModMessageReceiver {
    public FabricMessageReceiver(ModAdvancedLinks plugin, CommandSourceStack sender) {
        super(plugin, sender);
    }

    @Override
    protected boolean hasPermissionsNew() {
        try {
            // permissions() method from the sender's class, which returns a PermissionSet instance.
            Method permissionsMethod = sender.getClass().getMethod("method_75037");
            Object permissionSet = permissionsMethod.invoke(sender);

            // Permissions class and its field COMMANDS_OWNER.
            Class<?> permissionsClass = Class.forName("net.minecraft.class_12099");
            Field commandsOwnerField = permissionsClass.getField("field_63212");
            Object commandsOwnerPermission = commandsOwnerField.get(null);

            // hasPermission method (which has Permission as a param) from the PermissionSet class.
            Class<?> permissionSetClass = Class.forName("net.minecraft.class_12096");
            Class<?> permissionClass = Class.forName("net.minecraft.class_12087");
            Method hasPermissionMethod = permissionSetClass.getMethod("hasPermission", permissionClass);

            // Invoke the hasPermission method from the PermissionSet class passing COMMANDS_OWNER.
            return (boolean) hasPermissionMethod.invoke(permissionSet, commandsOwnerPermission);
        } catch (ReflectiveOperationException e) {
            plugin.logError(Level.SEVERE, "Couldn't get a user's permission level!", e);
            return false;
        }
    }
}