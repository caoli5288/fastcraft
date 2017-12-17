package co.kepler.fastcraft.compat;

import co.kepler.fastcraft.FastCraft;
import co.kepler.fastcraft.recipes.FastRecipe;
import co.kepler.fastcraft.recipes.custom.CustomRecipe;
import co.kepler.fastcraft.util.RecipeUtil;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

/**
 * Recipe compatibility class for FastCraft.
 */
public class Compat_FastCraft extends Compat {

    /**
     * Create a new compatibility instance for FastCraft.
     *
     * @param manager The manager this compatibility is associated with.
     */
    public Compat_FastCraft(RecipeCompatManager manager) {
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
        recipes.addAll(FastCraft.recipes().getRecipes());
        for (CustomRecipe r : FastCraft.recipes().getRecipes()) {
            if (r.getRecipe() == null) continue;
            getManager().addHandledRecipe(RecipeUtil.hashRecipe(r.getRecipe()));
        }
        return recipes;
    }
}
