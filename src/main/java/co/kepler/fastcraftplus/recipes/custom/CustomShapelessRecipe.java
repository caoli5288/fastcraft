package co.kepler.fastcraftplus.recipes.custom;

import co.kepler.fastcraftplus.config.RecipesConfig;
import co.kepler.fastcraftplus.recipes.Ingredient;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A shapeless recipe than supports ingredients with metadata.
 */
public class CustomShapelessRecipe extends CustomRecipe {
    private final List<ItemStack> results;
    private final ShapelessRecipe recipe;
    private final ItemStack[] matrix;

    /**
     * Create a new instance of CustomShapelessRecipe.
     *
     * @param result      The result of the recipe.
     * @param ingredients The ingredients of the recipe.
     * @throws RecipesConfig.RecipeException Thrown if the recipe is misconfigured.
     */
    public CustomShapelessRecipe(ItemStack result, List<Ingredient> ingredients) throws RecipesConfig.RecipeException {
        this.results = Collections.singletonList(result);

        // Count the number of ingredients required
        int ingredientCount = 0;
        for (Ingredient ing : ingredients) {
            addIngredient(ing);
            ingredientCount += ing.getAmount();
            if (ingredientCount > 9) break;
        }


        // Create Recipe if few enough ingredients
        if (ingredientCount <= 9) {
            matrix = new ItemStack[9];
            int matIndex = 0;
            recipe = new ShapelessRecipe(result);
            for (Ingredient ingredient : ingredients) {
                recipe.addIngredient(ingredient.getMaterialData());

                // Add items to matrix
                ItemStack matItem = ingredient.clone(1);
                for (int i = 0; i < ingredient.getAmount(); i++) {
                    matrix[matIndex++] = matItem;
                }
            }
        } else {
            recipe = null;
            matrix = null;
        }
    }

    @Override
    protected List<ItemStack> getResultsInternal() {
        return results;
    }

    @Override
    protected ShapelessRecipe getRecipeInternal() {
        return recipe;
    }

    @Override
    protected ItemStack[] getMatrixInternal() {
        return matrix;
    }

    @Override
    public boolean matchesMatrix(ItemStack[] matrix) {
        // List items in the matrix
        Map<ItemStack, Integer> matrixItems = new HashMap<>();
        for (ItemStack is : matrix) {
            if (is == null || is.getType() == Material.AIR) continue;
            ItemStack key = is.clone();
            key.setAmount(1);
            Integer amount = matrixItems.get(key);
            amount = (amount == null ? 0 : amount) + is.getAmount();
            matrixItems.put(key, amount);
        }

        // Remove ingredients from matrixItems
        for (Ingredient ing : getIngredients()) {
            ItemStack key = ing.clone();
            key.setAmount(1);
            Integer amount = matrixItems.get(key);
            amount = (amount == null ? 0 : amount) - ing.getAmount();
            if (amount < 0) return false;
            matrixItems.put(key, amount);
        }

        // Make sure there are no ingredients in matrixItems
        for (Integer i : matrixItems.values()) {
            if (i != null && i != 0) return false;
        }

        // Matches matrix
        return true;
    }

    @Override
    public boolean removeFromMatrix(ItemStack[] matrix) {
        if (!matchesMatrix(matrix)) return false;

        // Remove items from matrix
        for (int i = 0; i < matrix.length; i++) {
            ItemStack is = matrix[i];
            if (is == null || is.getType() == Material.AIR) continue;
            if (is.getAmount() <= 1) {
                matrix[i] = new ItemStack(Material.AIR);
            } else {
                is.setAmount(is.getAmount() - 1);
            }
        }
        return true;
    }
}
