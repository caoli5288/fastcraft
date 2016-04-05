package co.kepler.fastcraftplus.compat;

import co.kepler.fastcraftplus.config.Recipes;
import co.kepler.fastcraftplus.recipes.FastRecipe;
import co.kepler.fastcraftplus.recipes.custom.CustomRecipe;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Recipe;

import java.util.HashSet;
import java.util.Set;

/**
 * Recipe compatibility class for FastCraft+.
 */
public class Compat_FastCraftPlus extends Compat {

    /**
     * Create a new compatibility instance for FastCraft+.
     *
     * @param manager The manager this compatibility is associated with.
     */
    public Compat_FastCraftPlus(RecipeCompatManager manager) {
        super(manager);
    }

    @Override
    public boolean init() {
        return true;
    }

    @Override
    public String dependsOnPlugin() {
        return null;
    }

    @Override
    public Set<FastRecipe> getRecipes(Player player) {
        Set<FastRecipe> recipes = new HashSet<>();
        recipes.addAll(Recipes.getRecipes());
        return recipes;
    }
}
