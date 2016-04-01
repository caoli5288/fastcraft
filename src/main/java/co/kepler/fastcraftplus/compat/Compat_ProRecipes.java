package co.kepler.fastcraftplus.compat;

import co.kepler.fastcraftplus.recipes.FastRecipe;
import org.bukkit.entity.Player;

import java.util.Set;

/**
 * Recipe compatibility for ProRecipes.
 * <p>
 * Plugin: https://www.spigotmc.org/resources/9039/
 * API: https://www.spigotmc.org/wiki/prorecipes-recipeapi/
 */
public class Compat_ProRecipes extends Compat {

    @Override
    public boolean init() {
        return true;
    }

    @Override
    public String dependsOnPlugin() {
        return "ProRecipes";
    }

    @Override
    public Set<FastRecipe> getRecipes(Player player) {
        return null;
    }
}
