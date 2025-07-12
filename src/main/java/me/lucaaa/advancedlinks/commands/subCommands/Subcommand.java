package me.lucaaa.advancedlinks.commands.subCommands;

import me.lucaaa.advancedlinks.AdvancedLinks;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;

public abstract class Subcommand {
    protected final AdvancedLinks plugin;

    public Subcommand(AdvancedLinks plugin) {
        this.plugin = plugin;
    }

    // The name of the subcommand
    public abstract String name();

    // The description of the subcommand
    public abstract String description();

    // How the subcommand is used
    public abstract String usage();

    /*How many arguments are required to execute the subcommand (name not included)
     * For example:
     * [] required, () not required
     * /cmd giveSword [player] (customName) -> minArguments would be 1 */
    public abstract int minArguments();

    // The permission needed to run this command other than plugin.admin. Can be null (no permission needed)
    public abstract String neededPermission();

    /**
     * Gets the tab completions for a command in case you want to have any. Returns an empty list by default.
     *
     * @param sender The thing that is sending the command.
     * @param args The command's arguments to complete.
     * @return A list with the completions.
     */
    public ArrayList<String> getTabCompletions(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }

    /**
     * The method that will be run when the command is executed.
     *
     * @param sender The thing that sent the command.
     * @param args The command's arguments.
     */
    public abstract void run(CommandSender sender, String[] args);
}