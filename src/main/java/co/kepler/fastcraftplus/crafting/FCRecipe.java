package co.kepler.fastcraftplus.crafting;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

/**
 * A recipe that supports ingredients with metadata.
 */
public interface FCRecipe {

    /**
     * Get the result of this recipe.
     *
     * @return Returns the item crafted by this recipe.
     */
    ItemStack getResult();

    /**
     * Get the recipe.
     *
     * @return Returns the Recipe associated with this FCRecipe
     */
    Recipe getRecipe();

    /**
     * Get the GUI recipe.
     *
     * @return Returns the GUIRecipe associated with this FCRecipe.
     */
    GUIRecipe getGUIRecipe();

    /**
     * See if this recipe matches a crafting matrix.
     *
     * @param matrix The 3x3 matrix to check. Should be an array of length 9.
     * @return Returns true if this recipe matches the matrix.
     */
    boolean matchesMatrix(ItemStack[] matrix);

}
