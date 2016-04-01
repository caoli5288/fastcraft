package co.kepler.fastcraftplus.recipes.custom;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

/**
 * A recipe that supports ingredients with metadata.
 */
public interface CustomRecipe {

    /**
     * Get the result of this recipe.
     *
     * @return Returns the item crafted by this recipe.
     */
    ItemStack getResult();

    /**
     * Get the recipe.
     *
     * @return Returns the Recipe associated with this CustomRecipe
     */
    Recipe getRecipe();

    /**
     * See if this recipe matches a crafting matrix.
     *
     * @param matrix The 3x3 matrix to check. Should be an array of length 9.
     * @return Returns true if this recipe matches the matrix.
     */
    boolean matchesMatrix(ItemStack[] matrix);

}
