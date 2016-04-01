package co.kepler.fastcraftplus.compat;

import co.kepler.fastcraftplus.recipes.FastRecipe;
import org.bukkit.entity.Player;

import java.util.Set;

/**
 * An interface to be used by plugin compatibility classes.
 */
public abstract class Compat {

    /**
     * Called when the plugin compatibility is loaded.
     *
     * @return Returns true if loaded successfully
     */
    public abstract boolean init();

    /**
     * Get the name of the plugin. Return null if
     * not dependent on an external plugin.
     *
     * @return Returns the name of the plugin.
     */
    public abstract String getPluginName();

    /**
     * Get recipes provided by this compatibility.
     *
     * @param player The player who will be seeing the recipes.
     * @return Returns recipes provided by this compatibility, or null if there are none.
     */
    public abstract Set<FastRecipe> getRecipes(Player player);
}
