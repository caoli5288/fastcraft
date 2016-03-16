package co.kepler.fastcraftplus.crafting;

import org.bukkit.inventory.*;

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

    /**
     * Create a recipe from ingredients, a result, and byproducts.
     *
     * @param ingredients The ingredients used to craft this recipe.
     * @param result      The result of this recipe.
     * @param byproducts  The byproducts of this recipe.
     */
    public FastRecipe(Map<Ingredient, Integer> ingredients, ItemStack result, Set<ItemStack> byproducts) {
        this.ingredients = ingredients;
        this.result = result;
        this.byproducts = byproducts;
    }

    /**
     * Get the result of the recipe.
     *
     * @return The item crafted by this recipe.
     */
    public ItemStack getResult() {
        return result;
    }

    /**
     * Remove ingredients from an inventory.
     * @param inv The inventory to remove the ingredients from.
     * @return Returns true if the inventory had the necessary ingredients.
     */
    public boolean removeIngredients(Inventory inv) {
        Set<Ingredient> strictData = new HashSet<>();
        Set<Ingredient> anyData = new HashSet<>();

        // Sort ingredients into two sets.
        for (Ingredient i : ingredients.keySet()) {
            (i.anyData() ? anyData : strictData).add(i);
        }

        // Check ingredients with strict data.
        for (Ingredient i : strictData) {

        }

        return true; // TODO
    }
}
