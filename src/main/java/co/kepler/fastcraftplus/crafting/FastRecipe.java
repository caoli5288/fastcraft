package co.kepler.fastcraftplus.crafting;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Holds the ingredients and result of a recipe.
 */
public class FastRecipe {
    private Map<Ingredient, Integer> ingredients;
    private ItemStack result;
    private Set<ItemStack> byproducts;

    public FastRecipe(Recipe recipe) {
        ingredients = new HashMap<>();
        result = recipe.getResult();
        byproducts = new HashSet<>();

        // Get ingredients from recipe
        if (recipe instanceof ShapedRecipe) {
            ShapedRecipe r = (ShapedRecipe) recipe;
            for (String str : r.getShape()) {
                for (char c : str.toCharArray()) {
                    Ingredient i = new Ingredient(r.getIngredientMap().get(c));
                    ingredients.put(i, ingredients.getOrDefault(i, 0) + 1);
                }
            }
        } else if (recipe instanceof ShapelessRecipe) {
            ShapelessRecipe r = (ShapelessRecipe) recipe;
            for (ItemStack is : r.getIngredientList()) {
                Ingredient i = new Ingredient(is);
                ingredients.put(i, ingredients.getOrDefault(i, 0) + 1);
            }
        }

        // List the recipe's byproducts.
        for (Ingredient i : ingredients.keySet()) {
            switch (i.getMaterial()) {
                case LAVA_BUCKET:
                case MILK_BUCKET:
                case WATER_BUCKET:
                    byproducts.add(i.toItemStack(ingredients.get(i)));
            }
        }
    }
}
