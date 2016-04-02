package co.kepler.fastcraftplus.compat;

import co.kepler.fastcraftplus.recipes.FastRecipe;
import co.kepler.fastcraftplus.recipes.Ingredient;
import com.kirelcodes.ItemMaker.API.RecipeGetter;
import com.kirelcodes.ItemMaker.Recipes.Perfect.PerfectShapedRecipe;
import com.kirelcodes.ItemMaker.Recipes.Perfect.PerfefectShapelessRecipe;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * Recipe compatibility for Item Maker Pro.
 * <p>
 * Plugin: https://www.spigotmc.org/resources/7173/
 */
public class Compat_ItemMakerPro extends Compat {

    @Override
    public boolean init() {
        return true;
    }

    @Override
    public String dependsOnPlugin() {
        return "ItemMakerPro";
    }

    @Override
    public Set<FastRecipe> getRecipes(Player player) {
        Set<FastRecipe> recipes = new HashSet<>();

        // Add shaped recipes
        for (PerfectShapedRecipe recipe : RecipeGetter.getShapedRecipes()) {
            if (!recipe.hasPermission() || player.hasPermission(recipe.getPermission())) {
                // If player has permission to craft
                recipes.add(new FastRecipeCompat(recipe));
            }
        }

        // Add shapeless recipes
        for (PerfefectShapelessRecipe recipe : RecipeGetter.getShapelessRecipe()) {
            if (!recipe.hasPermission() || player.hasPermission(recipe.getPermission())) {
                // If player has permission to craft
                recipes.add(new FastRecipeCompat(recipe));
            }
        }

        return recipes;
    }

    public static class FastRecipeCompat extends FastRecipe {
        private final Map<Ingredient, Integer> ingredients = new HashMap<>();
        private final List<ItemStack> results;

        public FastRecipeCompat(PerfectShapedRecipe recipe) {
            results = Collections.singletonList(recipe.getResult());

            // Add ingredients
            for (ItemStack[] isArr : recipe.getItems()) {
                for (ItemStack is : isArr) {
                    Ingredient ingredient = new Ingredient(is);
                    Integer amount = ingredients.get(ingredient);
                    ingredients.put(ingredient, (amount == null ? 0 : amount) + is.getAmount());
                }
            }
        }

        public FastRecipeCompat(PerfefectShapelessRecipe recipe) {
            results = Collections.singletonList(recipe.getResult());

            // Add ingredients
            for (ItemStack is : recipe.getItems()) {
                Ingredient ingredient = new Ingredient(is);
                Integer amount = ingredients.get(ingredient);
                ingredients.put(ingredient, (amount == null ? 0 : amount) + is.getAmount());
            }
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
