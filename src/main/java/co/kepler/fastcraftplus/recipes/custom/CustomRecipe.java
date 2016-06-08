package co.kepler.fastcraftplus.recipes.custom;

import co.kepler.fastcraftplus.recipes.FastRecipe;
import org.bukkit.event.inventory.CraftItemEvent;
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
     * Remove ingredients from a crafting matrix for a CraftItemEvent.
     *
     * @param event The CraftItemEvent removing ingredients.
     */
    public abstract void removeFromMatrix(CraftItemEvent event);
}
