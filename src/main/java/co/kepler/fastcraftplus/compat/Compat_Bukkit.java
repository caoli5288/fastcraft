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
    private Map<Recipe, FastRecipe> recipes;
    private Set<Recipe> disabledRecipes;

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
        Set<FastRecipe> result = new HashSet<>();

        // Loop through the server's recipes
        for (Iterator<Recipe> iter = Bukkit.recipeIterator(); iter.hasNext();) {
            Recipe recipe = iter.next();


        }

        // Return a set of FastRecipes
        return result;
    }

    private FastRecipe getFastRecipe(Recipe recipe) {
        // See if this recipe has already been loaded.
        if (disabledRecipes.contains(recipe)) return null;
        if (recipes.containsKey(recipe)) return recipes.get(recipe);

        // Ignore recipes with null results
        if (recipe.getResult() == null) {
            disabledRecipes.add(recipe);
            return null;
        }

        // Load recipe
        if (recipe instanceof ShapedRecipe || recipe instanceof ShapelessRecipe) {
            // Create FastRecipe from a ShapedRecipe
            ShapedRecipe sr = (ShapedRecipe) recipe;

            // Get the matrix of items needed to craft this recipe
            ItemStack[] matrix = Recipe Util.getRecipeMatrix(sr);
            if (matrix == null) {
                disabledRecipes.add(recipe);
                return null;
            }

            // Get the result when crafting in a workbench
            ItemStack craftResult = RecipeUtil.getCraftingResult(sr, null);
            if (!sr.getResult().equals(craftResult)) {
                disabledRecipes.add(recipe);
                return null;
            }

            // Add recipe to recipes map
            FastRecipe result = new FastRecipeCompat(sr);
            recipes.put(recipe, result);
            return result;
        } else if (recipe instanceof ShapelessRecipe) {
            // Create FastRecipe from a ShapelessRecipe
            ShapelessRecipe sr = (ShapelessRecipe) recipe;

            // Get the result when crafting in a workbench
            ItemStack craftResult = RecipeUtil.getCraftingResult(sr, player);
            if (sr.getResult() != null && craftResult != null && !sr.getResult().equals(craftResult)) continue;

            // Get the matrix of items needed to craft this recipe
            ItemStack[] matrix = RecipeUtil.getRecipeMatrix(sr);
            if (matrix == null) continue;

            // If crafting the recipe isn't cancelled, create a new recipe
            if (RecipeUtil.callCraftItemEvent(player, sr, matrix, sr.getResult())) {
                result.add(new FastRecipeCompat(sr));
            }
        }
    }

    /**
     * Adapter class for Bukkit recipes.
     */
    public static class FastRecipeCompat extends FastRecipe {
        private final Map<Ingredient, Integer> ingredients = new HashMap<>();
        private final List<ItemStack> result;

        /**
         * Create a new FastRecipeCompat from a ShapedRecipe.
         *
         * @param recipe The Recipe this FastRecipe is based off of.
         */
        public FastRecipeCompat(Recipe recipe) {
            assert recipe instanceof ShapedRecipe || recipe instanceof ShapelessRecipe :
                    "Recipe must be a ShapedRecipe or a ShapelessRecipe";

            result = Collections.singletonList(recipe.getResult());

            // Fill map of ingredients
            if (recipe instanceof ShapedRecipe) {
                Recipe sr = (ShapedRecipe) recipe;
                for (String row : recipe.getShape()) {
                    for (char c : row.toCharArray()) {
                        ItemStack item = recipe.getIngredientMap().get(c);
                        if (item == null) continue;
                        Ingredient ingredient = new Ingredient(item);
                        Integer amount = ingredients.get(ingredient);
                        ingredients.put(ingredient, (amount == null ? 0 : amount) + 1);
                    }
                }
            }
        }

        /**
         * Create a new FastRecipeCompat from a ShapelessRecipe.
         *
         * @param recipe The Recipe this FastRecipe is based off of.
         */
        public FastRecipeCompat(ShapelessRecipe recipe) {
            result = Collections.singletonList(recipe.getResult());

            // Fill map of ingredients
            for (ItemStack item : recipe.getIngredientList()) {
                Ingredient ingredient = new Ingredient(item);
                Integer amount = ingredients.get(ingredient);
                ingredients.put(ingredient, (amount == null ? 0 : amount) + item.getAmount());
            }
        }

        @Override
        public Map<Ingredient, Integer> getIngredients() {
            return ingredients;
        }

        @Override
        public List<ItemStack> getResults() {
            return result;
        }
    }
}
