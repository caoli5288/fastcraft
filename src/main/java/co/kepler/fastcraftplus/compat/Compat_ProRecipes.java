package co.kepler.fastcraftplus.compat;

import co.kepler.fastcraftplus.recipes.FastRecipe;
import co.kepler.fastcraftplus.recipes.Ingredient;
import mc.mcgrizzz.prorecipes.ProRecipes;
import mc.mcgrizzz.prorecipes.RecipeAPI;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import java.util.*;

/**
 * Recipe compatibility for ProRecipes.
 * <p>
 * Plugin: https://www.spigotmc.org/resources/9039/
 * API: https://www.spigotmc.org/wiki/prorecipes-recipeapi/
 */
public class Compat_ProRecipes extends Compat {
    private Map<Integer, FastRecipe> recipes = new HashMap<>();
    private RecipeAPI.RecipeType[] recipeTypes;
    private RecipeAPI api;

    /**
     * Create a new compatibility instance for ProRecipes.
     *
     * @param manager The manager this compatibility is associated with.
     */
    public Compat_ProRecipes(RecipeCompatManager manager) {
        super(manager);
    }

    @Override
    public boolean init() {
        api = ProRecipes.getAPI();
        recipeTypes = new RecipeAPI.RecipeType[]{
                RecipeAPI.RecipeType.SHAPED,
                RecipeAPI.RecipeType.SHAPELESS,
                RecipeAPI.RecipeType.MULTI
        };
        return true;
    }

    @Override
    public String dependsOnPlugin() {
        return "ProRecipes";
    }

    @Override
    public Set<FastRecipe> getRecipes(Player player) {
        Set<FastRecipe> recipes = new HashSet<>();
        // Loop through all recipe types
        for (RecipeAPI.RecipeType type : recipeTypes) {
            // Loop through recipes of this type
            int count = api.recipeCount(type);
            for (int id = 0; id < count; id++) {
                // Get the recipe of this type and id
                RecipeAPI.RecipeContainer recipe = api.getRecipe(type, id);
                if (player == null || !recipe.hasPermission() || player.hasPermission(recipe.getPermission())) {
                    // If player has permission to craft
                    FastRecipe fastRecipe = getRecipe(recipe);
                    if (fastRecipe != null) recipes.add(fastRecipe);
                }
            }
        }
        return recipes;
    }

    public FastRecipe getRecipe(RecipeAPI.RecipeContainer recipe) {
        int hash = hash(recipe);
        if (!loadRecipe(recipe, hash)) return null;
        return recipes.get(hash);
    }

    public boolean loadRecipe(RecipeAPI.RecipeContainer recipe, int hash) {
        if (!recipes.containsKey(hash)) {
            recipes.put(hash, new FastRecipeCompat(recipe));
        }
        return true;
    }

    public int hash(RecipeAPI.RecipeContainer recipe) {
        int hash = 0;
        for (ItemStack is : recipe.getResult()) {
            if (is != null) hash += is.hashCode();
        }
        hash *= 31;
        for (ItemStack is : recipe.getIngredients()) {
            if (is != null) hash += is.hashCode();
        }
        return hash;
    }

    public static class FastRecipeCompat extends FastRecipe {
        private final Map<Ingredient, Integer> ingredients = new HashMap<>();
        private final List<ItemStack> results = new ArrayList<>();

        public FastRecipeCompat(RecipeAPI.RecipeContainer recipe) {
            Collections.addAll(results, recipe.getResult());
            for (ItemStack is : recipe.getIngredients()) {
                if (is == null) continue;
                Ingredient ingredient = new Ingredient(is);
                Integer amount = ingredients.get(ingredient);
                ingredients.put(ingredient, (amount == null ? 0 : amount) + is.getAmount());
            }
        }

        @Override
        public Recipe getRecipe() {
            return null;
        }

        @Override
        public ItemStack[] getMatrix() {
            return null;
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
