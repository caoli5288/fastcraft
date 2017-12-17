package co.kepler.fastcraft.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * A class to be extended by other command executors.
 */
public abstract class SimpleCommand {

    /**
     * Called when a command is run.
     *
     * @param sender The command sender.
     * @param args   The command arguments.
     * @return Returns true if the command was executed successfully.
     */
    public abstract boolean onCommand(CommandSender sender, String[] args);

    /**
     * Get tab complete options for a command argument.
     *
     * @param sender The command sender.
     * @param args   The command arguments.
     * @return The tab complete options.
     */
    public abstract List<String> onTabComplete(CommandSender sender, String[] args);

    /**
     * Copy an array, removing the first element.
     *
     * @param arr The array to remove the first element from.
     * @param <T> The class of the objects in the array.
     * @return Returns a new array with the first element removed.
     */
    protected <T> T[] popFirst(T[] arr) {
        assert arr != null && arr.length > 0 : "Nonempty array required";
        return Arrays.copyOfRange(arr, 1, arr.length);
    }

    /**
     * Find all the matches to a tab completed command argument.
     *
     * @param arg     The argument to find matches for.
     * @param toMatch The possible matches.
     * @return Returns a list of matches.
     */
    protected List<String> tabMatch(String arg, List<String> toMatch) {
        List<String> matches = new ArrayList<>();
        arg = arg.toLowerCase();
        for (String s : toMatch) {
            if (s.toLowerCase().startsWith(arg)) {
                matches.add(s);
            }
        }
        return matches;
    }

    protected List<String> tabMatchPlayer(String arg) {
        List<String> names = new ArrayList<>();
        for (Player p : Bukkit.getOnlinePlayers()) {
            names.add(p.getName());
        }
        Collections.sort(names, String.CASE_INSENSITIVE_ORDER);
        return tabMatch(arg, names);
    }
}
