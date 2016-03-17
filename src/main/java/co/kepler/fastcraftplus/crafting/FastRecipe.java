package co.kepler.fastcraftplus.crafting;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import java.util.*;

/**
 * Holds the ingredients and result of a recipe.
 */
public class FastRecipe {
    private Map<Ingredient, Integer> ingredients;
    private ItemStack result;
    private ItemStack[] results;

    public FastRecipe(Recipe recipe) {
        ingredients = new HashMap<>();
        result = recipe.getResult();

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
        List<ItemStack> byproducts = new ArrayList<>();
        for (Ingredient i : ingredients.keySet()) {
            switch (i.getMaterial()) {
                case LAVA_BUCKET:
                case MILK_BUCKET:
                case WATER_BUCKET:
                    byproducts.add(i.toItemStack(ingredients.get(i)));
            }
        }
        results = new ItemStack[byproducts.size() + 1];
        results[0] = result;
        for (int i = 0; i < byproducts.size(); i++) {
            results[i + 1] = byproducts.get(i);
        }
    }

    /**
     * Create a recipe from ingredients, a result, and byproducts.
     *
     * @param ingredients The ingredients used to craft this recipe.
     * @param result      The result of this recipe.
     * @param byproducts  The byproducts of this recipe.
     */
    public FastRecipe(Map<Ingredient, Integer> ingredients, ItemStack result, ItemStack... byproducts) {
        this.ingredients = ingredients;
        this.result = result;
        results = new ItemStack[byproducts.length + 1];

        results[0] = result;
        System.arraycopy(byproducts, 0, results, 1, byproducts.length);
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
     * Gets all the results of this recipe, including byproducts
     * like empty buckets from recipes that require filled buckets.
     *
     * @return Return the results of this recipe.
     */
    public ItemStack[] getResults() {
        return results;
    }

    /**
     * Remove ingredients from an inventory.
     *
     * @param items The items to remove the ingredients from.
     * @return Returns true if the inventory had the necessary ingredients.
     */
    private boolean removeIngredients(ItemStack[] items) {
        LinkedList<Ingredient> toRemove = new LinkedList<Ingredient>();

        // Add ingredients. Those that can use any data go at the end.
        for (Ingredient i : ingredients.keySet()) {
            if (i.anyData()) {
                toRemove.addLast(i);
            } else {
                toRemove.addFirst(i);
            }
        }

        // Remove ingredients.
        for (Ingredient i : toRemove) {
            if (!i.removeIngredients(items, ingredients.get(i))) {
                // If unable to remove all of this ingredient
                return false;
            }
        }

        return true;
    }

    /**
     * See if a player has this recipe's ingredients, and optionally, remove them
     * from the player's inventory if all ingredients are present.
     *
     * @param player The player whose inventory will have ingredients removed.
     * @param remove Whether ingredients should be removed if all exist in the player's inventory.
     * @return Returns true if the ingredients were removed from the player's inventory.
     */
    public boolean canCraft(Player player, boolean remove) {
        ItemStack[] contents = player.getInventory().getContents();
        for (int i = 0; i < contents.length; i++) {
            if (contents[i] == null) continue;
            contents[i] = contents[i].clone();
        }
        boolean result = removeIngredients(contents);
        if (result && remove) {
            player.getInventory().setContents(contents);
        }
        return result;
    }
}
