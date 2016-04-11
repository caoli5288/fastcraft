package co.kepler.fastcraftplus.commands;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A class to be extended by other command executors.
 */
public abstract class Command implements CommandExecutor, TabCompleter {

    /**
     * Copy an array, removing the first element.
     *
     * @param arr The array to remove the first element from.
     * @param <T> The class of the objects in the array.
     * @return Returns a new array with the first element removed.
     */
    protected <T> T[] popFirst(T[] arr) {
        assert arr != null && arr.length > 0 : "Nonempty array required";
        return Arrays.copyOfRange(arr, 1, arr.length - 1);
    }

    /**
     * Find all the matches to a tab completed command argument.
     *
     * @param arg The argument to find matches for.
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
}
