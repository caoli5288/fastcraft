package co.kepler.fastcraftplus.recipes.custom;

import co.kepler.fastcraftplus.recipes.FastRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

/**
 * A recipe that supports ingredients with metadata.
 */
public abstract class CustomRecipe extends FastRecipe {

    /**
     * Get the recipe.
     *
     * @return Returns the Recipe associated with this CustomRecipe
     */
    public abstract Recipe getRecipe();

    /**
     * See if this recipe matches a crafting matrix.
     *
     * @param matrix The 3x3 matrix to check. Should be an array of length 9.
     * @return Returns true if this recipe matches the matrix.
     */
    public abstract boolean matchesMatrix(ItemStack[] matrix);

}
