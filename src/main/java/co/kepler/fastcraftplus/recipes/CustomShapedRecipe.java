package co.kepler.fastcraftplus.recipes;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import java.util.HashMap;
import java.util.Map;

/**
 * A shaped recipe than supports input items with metadata.
 */
public class CustomShapedRecipe extends ShapedRecipe {
    Map<Character, ItemStack> ingredients = new HashMap<>();

    public CustomShapedRecipe(ItemStack result) {
        super(result);
    }

    /**
     * Sets the ingredient that a character in the recipe shape refers to.
     *
     * @param key        The character that represents the ingredient in the shape
     * @param ingredient The ingredient
     * @return The changed recipe, so you can chain calls.
     */
    public CustomShapedRecipe setIngredient(char key, ItemStack ingredient) {
        ingredients.put(key, ingredient.clone());
        return this;
    }

    @Override
    public Map<Character, ItemStack> getIngredientMap() {
        Map<Character, ItemStack> result = super.getIngredientMap();
        for (Character c : ingredients.keySet()) {
            result.put(c, ingredients.get(c).clone());
        }
        return result;
    }
}
