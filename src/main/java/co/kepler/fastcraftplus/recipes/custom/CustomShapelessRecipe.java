package co.kepler.fastcraftplus.recipes.custom;

import co.kepler.fastcraftplus.config.RecipesConfig;
import co.kepler.fastcraftplus.recipes.Ingredient;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;

import java.util.Collections;
import java.util.List;

/**
 * A shapeless recipe than supports ingredients with metadata.
 */
public class CustomShapelessRecipe extends CustomRecipe {
    private final List<ItemStack> results;
    private final ShapelessRecipe recipe;
    private final List<Ingredient> ingredients;
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
        this.ingredients = ingredients;

        // Count the number of ingredients required
        int ingredientCount = 0;
        for (Ingredient ing : ingredients) {
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
    protected List<Ingredient> getIngredientsInternal() {
        return ingredients;
    }

    @Override
    protected ItemStack[] getMatrixInternal() {
        return matrix;
    }

    @Override
    public boolean matchesMatrix(ItemStack[] matrix) {
        // Clone matrix, and set all items in matrix to null, or set amount to 1
        matrix = matrix.clone();
        for (int i = 0; i < matrix.length; i++) {
            if (matrix[i] == null) continue;
            if (matrix[i].getAmount() <= 0) {
                matrix[i] = null;
            } else {
                matrix[i] = matrix[i].clone();
                matrix[i].setAmount(1);
            }
        }

        // Make sure all ingredients exist, and that there aren't extra items
        if (!removeIngredients(matrix, 1)) return false;
        for (ItemStack is : matrix) {
            if (is != null) return false;
        }

        // Matches the matrix
        return true;
    }
}
