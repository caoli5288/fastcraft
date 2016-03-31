package co.kepler.fastcraftplus.recipes;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A recipe that will be used by the FastCraft+ user interface.
 */
public abstract class FastRecipe {

    /**
     * Get the ingredients required to craft this recipe.
     *
     * @return Returns the ingredients required to craft this recipe.
     */
    public abstract Map<Ingredient, Integer> getIngredients();

    /**
     * Get the result of this recipe.
     *
     * @return Returns the result of this recipe.
     */
    public abstract ItemStack getResult();

    /**
     * Get the byproducts of this recipe. By default, returns one empty bucket
     * for every non-empty bucket used in the recipe.
     *
     * @return Returns the results of this recipe.
     */
    public List<ItemStack> getByproducts() {
        List<ItemStack> result = new ArrayList<>();
        
        // Count the number of buckets to be returned
        int buckets = 0;
        for (Ingredient i : getIngredients().keySet()) {
            switch (i.getMaterial()) {
                case LAVA_BUCKET:
                case MILK_BUCKET:
                case WATER_BUCKET:
                    buckets += getIngredients().get(i);
            }
        }

        // Add buckets to the result
        int stackSize = Material.BUCKET.getMaxStackSize();
        while (buckets > stackSize) {
            result.add(new ItemStack(Material.BUCKET, stackSize));
        }
        result.add(new ItemStack(Material.BUCKET, buckets));

        // Return the list of byproducts
        return result;
    }
}
