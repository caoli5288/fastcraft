package co.kepler.fastcraftplus.compat;

import co.kepler.fastcraftplus.recipes.FastRecipe;
import co.kepler.fastcraftplus.recipes.Ingredient;
import co.kepler.fastcraftplus.util.RecipeUtil;
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
    private final Map<Integer, FastRecipe> recipes = new HashMap<>();

    /**
     * Create a new compatibility instance for Bukkit.
     *
     * @param manager The manager this compatibility is associated with.
     */
    public Compat_Bukkit(RecipeCompatManager manager) {
        super(manager);
    }

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
        for (Iterator<Recipe> iter = Bukkit.recipeIterator(); iter.hasNext(); ) {
            FastRecipe recipe = getRecipe(iter.next());
            if (recipe != null) result.add(recipe);
        }

        // Return a set of FastRecipes
        return result;
    }

    /**
     * Get a FastRecipe from the given Recipe.
     *
     * @param recipe The Recipe to get a FastRecipe from.
     * @return Returns a FastRecipe, or null if unable.
     */
    protected FastRecipe getRecipe(Recipe recipe) {
        int hash = RecipeUtil.hashRecipe(recipe);
        return loadRecipe(recipe, hash) ? recipes.get(hash) : null;
    }

    /**
     * Load a recipe, and store it for later access by getRecipe.
     *
     * @param recipe The recipe to load.
     * @return Returns true if the recipe was successfully loaded, or if it was already loaded.
     */
    protected boolean loadRecipe(Recipe recipe, int hash) {
        // See if this recipe has already been loaded.
        if (recipes.containsKey(hash)) return true;

        // Ignore recipe if it's already been handled by another compatibility
        if (getManager().isRecipeHandled(hash)) return false;

        // Ignore recipes with null results
        if (recipe.getResult() == null) return false;

        // Ignore special recipes
        if (RecipeUtil.shouldIgnoreRecipe(hash)) return false;

        // Load recipe
        if (recipe instanceof ShapedRecipe || recipe instanceof ShapelessRecipe) {
            // Get the matrix of items needed to craft this recipe
            ItemStack[] matrix = RecipeUtil.getRecipeMatrix(recipe);
            if (matrix == null) return false;

            // Add recipe to recipes map
            FastRecipe result = new FastRecipeCompat(recipe, matrix);
            recipes.put(hash, result);
            getManager().addHandledRecipe(hash);
            return true;
        }
        return false;
    }

    /**
     * Adapter class for Bukkit recipes.
     */
    public static class FastRecipeCompat extends FastRecipe {
        private final List<ItemStack> result;
        private final Recipe recipe;
        private final ItemStack[] matrix;

        /**
         * Create a new FastRecipeCompat from a ShapedRecipe.
         *
         * @param recipe The Recipe this FastRecipe is based off of.
         */
        public FastRecipeCompat(Recipe recipe, ItemStack[] matrix) {
            assert recipe instanceof ShapedRecipe || recipe instanceof ShapelessRecipe :
                    "Recipe must be a ShapedRecipe or a ShapelessRecipe";

            // Set the result and the matrix of this item
            result = Collections.singletonList(recipe.getResult());
            this.recipe = recipe;
            this.matrix = matrix;

            // Fill map of ingredients
            if (recipe instanceof ShapedRecipe) {
                // Get ingredients from ShapedRecipe
                ShapedRecipe sr = (ShapedRecipe) recipe;
                for (String row : sr.getShape()) {
                    for (char c : row.toCharArray()) {
                        ItemStack item = sr.getIngredientMap().get(c);
                        if (item == null) continue;
                        addIngredient(new Ingredient(item));
                    }
                }
            } else {
                // Get ingredients from ShapelessRecipe
                ShapelessRecipe sr = (ShapelessRecipe) recipe;
                for (ItemStack item : sr.getIngredientList()) {
                    addIngredient(new Ingredient(item));
                }
            }
        }

        @Override
        protected Recipe getRecipeInternal() {
            return recipe;
        }

        @Override
        protected ItemStack[] getMatrixInternal() {
            return matrix;
        }

        @Override
        protected List<ItemStack> getResultsInternal() {
            return result;
        }
    }
}
