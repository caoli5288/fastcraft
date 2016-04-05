package co.kepler.fastcraftplus.compat;

import co.kepler.fastcraftplus.recipes.FastRecipe;
import co.kepler.fastcraftplus.recipes.Ingredient;
import co.kepler.fastcraftplus.recipes.RecipeUtil;
import com.kirelcodes.ItemMaker.API.RecipeGetter;
import com.kirelcodes.ItemMaker.Recipes.Perfect.PerfectShapedRecipe;
import com.kirelcodes.ItemMaker.Recipes.Perfect.PerfectShapelessRecipe;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import java.util.*;

/**
 * Recipe compatibility for Item Maker Pro.
 * <p>
 * Plugin: https://www.spigotmc.org/resources/7173/
 */
public class Compat_ItemMakerPro extends Compat {
    private final Map<Integer, FastRecipe> recipes = new HashMap<>();

    /**
     * Create a new compatibility instance for Item Maker Pro.
     *
     * @param manager The manager this compatibility is associated with.
     */
    public Compat_ItemMakerPro(RecipeCompatManager manager) {
        super(manager);
    }

    @Override
    public boolean init() {
        getRecipes(null);
        return true;
    }

    @Override
    public String dependsOnPlugin() {
        return "ItemMakerPro";
    }

    @Override
    public Set<FastRecipe> getRecipes(Player player) {
        Set<FastRecipe> recipes = new HashSet<>();

        // Get PerfectShapedRecipes
        for (PerfectShapedRecipe recipe : RecipeGetter.getShapedRecipes()) {
            if (player == null || !recipe.hasPermission() || player.hasPermission(recipe.getPermission())) {
                // If player is null, or if player has permission to craft
                recipes.add(getRecipe(recipe));
            }
        }

        // Get PerfectShapelessRecipes
        for (PerfectShapelessRecipe recipe : RecipeGetter.getShapelessRecipe()) {
            if (player == null || !recipe.hasPermission() || player.hasPermission(recipe.getPermission())) {
                // If player is null, or if player has permission to craft
                recipes.add(getRecipe(recipe));
            }
        }

        return recipes;
    }

    /**
     * Get a FastRecipe from the given Recipe.
     *
     * @param recipe The Recipe to get a FastRecipe from.
     * @return Returns a FastRecipe, or null if unable.
     */
    private FastRecipe getRecipe(PerfectShapedRecipe recipe) {
        int hash = RecipeUtil.hashRecipe(recipe.getRecipe());
        if (!loadRecipe(recipe, hash)) return null;
        return recipes.get(hash);
    }

    /**
     * Get a FastRecipe from the given Recipe.
     *
     * @param recipe The Recipe to get a FastRecipe from.
     * @return Returns a FastRecipe, or null if unable.
     */
    private FastRecipe getRecipe(PerfectShapelessRecipe recipe) {
        int hash = RecipeUtil.hashRecipe(recipe.getRecipe());
        if (!loadRecipe(recipe, hash)) return null;
        return recipes.get(hash);
    }

    /**
     * Load a recipe, and store it for later access by getRecipe.
     *
     * @param recipe The recipe to load.
     * @return Returns true if the recipe was successfully loaded, or if it was already loaded.
     */
    private boolean loadRecipe(PerfectShapedRecipe recipe, int hash) {
        if (recipes.containsKey(hash)) return true;
        recipes.put(hash, new FastRecipeCompat(recipe));
        getManager().addHandledRecipe(hash);
        return true;
    }

    /**
     * Load a recipe, and store it for later access by getRecipe.
     *
     * @param recipe The recipe to load.
     * @return Returns true if the recipe was successfully loaded, or if it was already loaded.
     */
    private boolean loadRecipe(PerfectShapelessRecipe recipe, int hash) {
        if (recipes.containsKey(hash)) return true;
        recipes.put(hash, new FastRecipeCompat(recipe));
        getManager().addHandledRecipe(hash);
        return true;
    }

    public static class FastRecipeCompat extends FastRecipe {
        private final Map<Ingredient, Integer> ingredients = new HashMap<>();
        private final List<ItemStack> results;
        private final Recipe recipe;
        private final ItemStack[] matrix;

        public FastRecipeCompat(PerfectShapedRecipe recipe) {
            results = Collections.singletonList(recipe.getResult());
            this.recipe = recipe.getRecipe();

            // Add ingredients
            ItemStack[][] items = recipe.getItems();
            ItemStack[] matrix = items.length > 3 ? null : new ItemStack[9];
            for (int row = 0; row < items.length; row++) {
                ItemStack[] curRow = items[row];
                if (curRow.length > 3) matrix = null;
                for (int col = 0; col < items[row].length; col++) {
                    ItemStack is = items[row][col];
                    Ingredient ingredient = new Ingredient(is);
                    Integer amount = ingredients.get(ingredient);
                    ingredients.put(ingredient, (amount == null ? 0 : amount) + is.getAmount());
                    if (matrix != null) {
                        matrix[row * 3 + col] = is;
                    }
                }
            }

            // Set the matrix
            this.matrix = matrix;
        }

        public FastRecipeCompat(PerfectShapelessRecipe recipe) {
            results = Collections.singletonList(recipe.getResult());
            this.recipe = recipe.getRecipe();

            // Add ingredients
            for (ItemStack is : recipe.getItems()) {
                Ingredient ingredient = new Ingredient(is);
                Integer amount = ingredients.get(ingredient);
                ingredients.put(ingredient, (amount == null ? 0 : amount) + is.getAmount());
            }

            // Fill matrix
            ItemStack[] matrix = new ItemStack[9];
            int matIndex = 0;
            for (Ingredient ingredient : ingredients.keySet()) {
                if (matIndex >= matrix.length) {
                    matrix = null;
                    break;
                }
                ItemStack curItem = ingredient.toItemStack(1);
                for (int i = 0; i < ingredients.get(ingredient); i++) {
                    matrix[matIndex++] = curItem;
                }
            }
            this.matrix = matrix;
        }

        @Override
        public Recipe getRecipe() {
            return recipe;
        }

        @Override
        public ItemStack[] getMatrix() {
            return matrix;
        }

        @Override
        public Map<Ingredient, Integer> getIngredients() {
            return ingredients;
        }

        @Override
        public List<ItemStack> getResults() {
            return results;
        }
    }
}
