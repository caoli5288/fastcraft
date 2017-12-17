package co.kepler.fastcraft.compat;

import co.kepler.fastcraft.recipes.FastRecipe;
import org.bukkit.entity.Player;

import java.util.Set;

/**
 * An interface to be used by plugin compatibility classes.
 */
public abstract class Compat {
    private final RecipeCompatManager manager;

    /**
     * Create a new compatibility instance.
     *
     * @param manager The manager this compatibility is associated with.
     */
    public Compat(RecipeCompatManager manager) {
        this.manager = manager;
    }

    /**
     * Get the recipe compatibility manager this compat is associated with.
     *
     * @return Get this compat's recipe compatibility manager.
     */
    protected final RecipeCompatManager getManager() {
        return manager;
    }

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
    public abstract String dependsOnPlugin();

    /**
     * Get recipes provided by this compatibility.
     *
     * @param player The player who will be seeing the recipes. Null if loading recipes.
     * @return Returns recipes provided by this compatibility, or null if there are none.
     */
    public abstract Set<FastRecipe> getRecipes(Player player);
}
