package co.kepler.fastcraftplus.compat;

import co.kepler.fastcraftplus.recipes.FastRecipe;
import co.kepler.fastcraftplus.recipes.Ingredient;
import co.kepler.fastcraftplus.recipes.RecipeUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Recipe compatibility for Item Maker Pro.
 * <p/>
 * Plugin: https://www.spigotmc.org/resources/7173/
 */
public class Compat_ItemMakerPro extends Compat {
    private final Map<Integer, FastRecipe> recipes = new HashMap<>();

    private RecipeGetter recipeGetter;

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
        try {
            recipeGetter = new RecipeGetter();
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            e.printStackTrace();
            return false;
        }
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
        for (PerfectShapedRecipe recipe : recipeGetter.getShapedRecipes()) {
            if (recipe == null) continue;
            if (player == null || !recipe.hasPermission() || player.hasPermission(recipe.getPermission())) {
                // If player is null, or if player has permission to craft
                recipes.add(getRecipe(recipe));
            }
        }

        // Get PerfectShapelessRecipes
        for (PerfectShapelessRecipe recipe : recipeGetter.getShapelessRecipe()) {
            if (recipe == null) continue;
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
                    addIngredient(new Ingredient(is));
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
                addIngredient(new Ingredient(is));
            }

            // Fill matrix
            ItemStack[] matrix = new ItemStack[9];
            int matIndex = 0;
            for (Ingredient ingredient : getIngredients()) {
                if (matIndex >= matrix.length) {
                    matrix = null;
                    break;
                }
                ItemStack curItem = ingredient.clone(1);
                for (int i = 0; i < ingredient.getAmount(); i++) {
                    matrix[matIndex++] = curItem;
                }
            }
            this.matrix = matrix;
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
            return results;
        }
    }

    private class RecipeGetter {
        private final Method
                methodGetShapedRecipes,
                methodGetShapelessRecipe,
                methodShapedGetRecipe,
                methodShapedHasPermission,
                methodShapedGetPermission,
                methodShapedGetItems,
                methodShapedGetResult,
                methodShapelessGetRecipe,
                methodShapelessHasPermission,
                methodShapelessGetPermission,
                methodShapelessGetItems,
                methodShapelessGetResult;

        public RecipeGetter() throws ClassNotFoundException, NoSuchMethodException {
            Class<?> classRecipeGetter = Class.forName("com.kirelcodes.ItemMaker.API.RecipeGetter");
            methodGetShapedRecipes = classRecipeGetter.getMethod("getShapedRecipes");
            methodGetShapelessRecipe = classRecipeGetter.getMethod("getShapelessRecipe");

            Class<?> classShaped = Class.forName("com.kirelcodes.ItemMaker.Recipes.Perfect.PerfectShapedRecipe");
            methodShapedGetRecipe = classShaped.getMethod("getRecipe");
            methodShapedHasPermission = classShaped.getMethod("hasPermission");
            methodShapedGetPermission = classShaped.getMethod("getPermission");
            methodShapedGetItems = classShaped.getMethod("getItems");
            methodShapedGetResult = classShaped.getMethod("getResult");

            Class<?> classShapeless = Class.forName("com.kirelcodes.ItemMaker.Recipes.Perfect.PerfectShapelessRecipe");
            methodShapelessGetRecipe = classShapeless.getMethod("getRecipe");
            methodShapelessHasPermission = classShapeless.getMethod("hasPermission");
            methodShapelessGetPermission = classShapeless.getMethod("getPermission");
            methodShapelessGetItems = classShapeless.getMethod("getItems");
            methodShapelessGetResult = classShapeless.getMethod("getResult");
        }

        @SuppressWarnings("unchecked")
        public List<PerfectShapedRecipe> getShapedRecipes() {
            List<PerfectShapedRecipe> result = new ArrayList<>();
            try {
                for (Object recipe : (List<Object>) methodGetShapedRecipes.invoke(null)) {
                    result.add(new PerfectShapedRecipe(
                            (Recipe) methodShapedGetRecipe.invoke(recipe),
                            (boolean) methodShapedHasPermission.invoke(recipe),
                            (String) methodShapedGetPermission.invoke(recipe),
                            (ItemStack[][]) methodShapedGetItems.invoke(recipe),
                            (ItemStack) methodShapedGetResult.invoke(recipe)
                    ));
                }
            } catch (IllegalAccessException | InvocationTargetException | ClassCastException e) {
                e.printStackTrace();
            }
            return result;
        }

        @SuppressWarnings("unchecked")
        public List<PerfectShapelessRecipe> getShapelessRecipe() {
            List<PerfectShapelessRecipe> result = new ArrayList<>();
            try {
                for (Object recipe : (List<Object>) methodGetShapelessRecipe.invoke(null)) {
                    result.add(new PerfectShapelessRecipe(
                            (Recipe) methodShapelessGetRecipe.invoke(recipe),
                            (boolean) methodShapelessHasPermission.invoke(recipe),
                            (String) methodShapelessGetPermission.invoke(recipe),
                            (List<ItemStack>) methodShapelessGetItems.invoke(recipe),
                            (ItemStack) methodShapelessGetResult.invoke(recipe)
                    ));
                }
            } catch (IllegalAccessException | InvocationTargetException | ClassCastException e) {
                e.printStackTrace();
            }
            return result;
        }
    }

    private class PerfectShapedRecipe {
        private final Recipe recipe;
        private final boolean hasPermission;
        private final String permission;
        private final ItemStack[][] items;
        private final ItemStack result;

        public PerfectShapedRecipe(Recipe recipe, boolean hasPermission, String permission,
                                   ItemStack[][] items, ItemStack result) {
            this.recipe = recipe;
            this.hasPermission = hasPermission;
            this.permission = permission;
            this.items = items;
            this.result = result;
        }

        public Recipe getRecipe() {
            return recipe;
        }

        public boolean hasPermission() {
            return hasPermission;
        }

        public String getPermission() {
            return permission;
        }

        public ItemStack[][] getItems() {
            return items;
        }

        public ItemStack getResult() {
            return result;
        }
    }

    private class PerfectShapelessRecipe {
        private final Recipe recipe;
        private final boolean hasPermission;
        private final String permission;
        private final List<ItemStack> items;
        private final ItemStack result;

        public PerfectShapelessRecipe(Recipe recipe, boolean hasPermission, String permission,
                                      List<ItemStack> items, ItemStack result) {
            this.recipe = recipe;
            this.hasPermission = hasPermission;
            this.permission = permission;
            this.items = items;
            this.result = result;
        }

        public Recipe getRecipe() {
            return recipe;
        }

        public boolean hasPermission() {
            return hasPermission;
        }

        public String getPermission() {
            return permission;
        }

        public List<ItemStack> getItems() {
            return items;
        }

        public ItemStack getResult() {
            return result;
        }
    }
}
