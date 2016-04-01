package co.kepler.fastcraftplus.compat;

import co.kepler.fastcraftplus.recipes.FastRecipe;
import co.kepler.fastcraftplus.recipes.Ingredient;
import co.kepler.fastcraftplus.recipes.RecipeUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import java.util.*;

/**
 * Recipe compatibility class for Bukkit.
 */
public class Compat_Bukkit extends Compat {

    @Override
    public boolean init() {
        return true;
    }

    @Override
    public String dependsOnPlugin() {
        return null;
    }

    @Override
    public Set<FastRecipe> getRecipes(Player player) {
        Set<FastRecipe> result = new HashSet<FastRecipe>();

        // Loop through the server's recipes
        for (Iterator<Recipe> iter = Bukkit.recipeIterator(); iter.hasNext();) {
            Recipe recipe = iter.next();
            FastRecipe newRecipe = null;

            if (recipe instanceof ShapedRecipe) {
                // Create FastRecipe from a ShapedRecipe
                ShapedRecipe sr = (ShapedRecipe) recipe;
                ItemStack craftResult = RecipeUtil.getCraftingResult(sr, player);
                if (craftResult != null && craftResult.equals(sr.getResult())) {
                    newRecipe = new FastRecipeCompat(sr);
                }
            } else if (recipe instanceof ShapelessRecipe) {
                // Create FastRecipe from a ShapelessRecipe
                ShapelessRecipe sr = (ShapelessRecipe) recipe;
                ItemStack craftResult = RecipeUtil.getCraftingResult(sr, player);
                if (craftResult != null && craftResult.equals(sr.getResult())) {
                    newRecipe = new FastRecipeCompat(sr);
                }
            } else {
                continue;
            }

            // Add the new recipe to the result
            if (newRecipe != null) {
                result.add(newRecipe);
            }
        }

        // Return a list of FastRecipes
        return result;
    }

    /**
     * Adapter class for Bukkit recipes.
     */
    public static class FastRecipeCompat extends FastRecipe {
        private final Map<Ingredient, Integer> ingredients = new HashMap<>();
        private final Recipe recipe;

        /**
         * Create a new FastRecipeCompat from a ShapedRecipe.
         *
         * @param recipe The Recipe this FastRecipe is based off of.
         */
        public FastRecipeCompat(ShapedRecipe recipe) {
            this.recipe = recipe;

            // Fill map of ingredients
            for (String row : recipe.getShape()) {
                for (char c : row.toCharArray()) {
                    Ingredient ingredient = new Ingredient(recipe.getIngredientMap().get(c));
                    Integer amount = ingredients.get(ingredient);
                    ingredients.put(ingredient, (amount == null ? 0 : amount) + 1);
                }
            }
        }

        /**
         * Create a new FastRecipeCompat from a ShapelessRecipe.
         *
         * @param recipe The Recipe this FastRecipe is based off of.
         */
        public FastRecipeCompat(ShapelessRecipe recipe) {
            this.recipe = recipe;

            // Fill map of ingredients
            for (ItemStack item : recipe.getIngredientList()) {
                Ingredient ingredient = new Ingredient(item);
                Integer amount = ingredients.get(ingredient);
                ingredients.put(ingredient, (amount == null ? 0 : amount) + item.getAmount());
            }
        }

        @Override
        public Recipe getRecipe() {
            return recipe;
        }

        @Override
        public Map<Ingredient, Integer> getIngredients() {
            return ingredients;
        }

        @Override
        public ItemStack getResult() {
            return recipe.getResult();
        }
    }
}
