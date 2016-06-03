package co.kepler.fastcraftplus.recipes.custom;

import co.kepler.fastcraftplus.recipes.FastRecipe;
import org.bukkit.inventory.ItemStack;

/**
 * A recipe that supports ingredients with metadata.
 */
public abstract class CustomRecipe extends FastRecipe {

    /**
     * See if this recipe matches a crafting matrix.
     *
     * @param matrix The 3x3 matrix to check. Should be an array of length 9.
     * @return Returns true if this recipe matches the matrix.
     */
    public abstract boolean matchesMatrix(ItemStack[] matrix);

    /**
     * Remove items from a crafting matrix.
     *
     * @param matrix The matrix to remove items from.
     * @return Returns true if able to remove items from matrix.
     */
    public abstract boolean removeFromMatrix(ItemStack[] matrix);
}
