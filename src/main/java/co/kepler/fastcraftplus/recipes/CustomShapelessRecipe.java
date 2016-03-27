package co.kepler.fastcraftplus.recipes;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;

import java.util.ArrayList;
import java.util.List;

/**
 * A shapeless recipe than supports input items with metadata.
 */
public class CustomShapelessRecipe extends ShapelessRecipe {
    public List<ItemStack> ingredients = new ArrayList<>();

    public CustomShapelessRecipe(ItemStack result) {
        super(result);
    }

    /**
     * Add an ItemStack as an ingredient
     *
     * @param ingredient The ingredient to add.
     * @return The changed recipe, so you can chain calls.
     */
    public CustomShapelessRecipe addIngredient(ItemStack ingredient) {
        ingredients.add(ingredient.clone());
        return this;
    }

    @Override
    public List<ItemStack> getIngredientList() {
        List<ItemStack> result = super.getIngredientList();
        for (ItemStack is : ingredients) {
            result.add(is.clone());
        }
        return result;
    }
}
