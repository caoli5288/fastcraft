package co.kepler.fastcraftplus.compat;

import co.kepler.fastcraftplus.recipes.FastRecipe;
import co.kepler.fastcraftplus.recipes.Ingredient;
import mc.mcgrizzz.prorecipes.ProRecipes;
import mc.mcgrizzz.prorecipes.RecipeAPI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import java.util.*;

/**
 * Recipe compatibility for ProRecipes.
 * <p/>
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
                if (allNullOrAir(recipe.getResult())) continue;
                if (player == null || !recipe.hasPermission() || player.hasPermission(recipe.getPermission())) {
                    // If player has permission to craft
                    FastRecipe fastRecipe = getRecipe(recipe);
                    if (fastRecipe != null) recipes.add(fastRecipe);
                }
            }
        }
        return recipes;
    }

    public boolean allNullOrAir(ItemStack... items) {
        for (ItemStack is : items)
            if (is != null && is.getType() != Material.AIR)
                return false;
        return true;
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
        private final List<ItemStack> results;

        public FastRecipeCompat(RecipeAPI.RecipeContainer recipe) {
            results = new ArrayList<>(4);
            for (ItemStack result : recipe.getResult()) {
                if (result != null && result.getType() != Material.ACACIA_DOOR)
                    results.add(result);
            }

            for (ItemStack is : recipe.getIngredients()) {
                if (is == null) continue;
                addIngredient(new Ingredient(is));
            }
        }

        @Override
        protected Recipe getRecipeInternal() {
            return null;
        }

        @Override
        protected ItemStack[] getMatrixInternal() {
            return null;
        }

        @Override
        protected List<ItemStack> getResultsInternal() {
            return results;
        }
    }
}
