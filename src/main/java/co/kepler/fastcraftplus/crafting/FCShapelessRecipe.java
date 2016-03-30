package co.kepler.fastcraftplus.crafting;

import co.kepler.fastcraftplus.config.Recipes;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;

import java.util.Map;

/**
 * A shapeless recipe than supports ingredients with metadata.
 */
public class FCShapelessRecipe implements FCRecipe {
    private final ItemStack result;
    private final ShapelessRecipe recipe;
    private final GUIRecipe guiRecipe;

    /**
     * Create a new instance of FCShapelessRecipe.
     *
     * @param result      The result of the recipe.
     * @param ingredients The ingredients of the recipe.
     * @throws Recipes.RecipeException Thrown if the recipe is misconfigured.
     */
    public FCShapelessRecipe(ItemStack result, Map<Ingredient, Integer> ingredients) throws Recipes.RecipeException {
        this.result = result;

        // Check that there aren't too many ingredients.
        int totalIngredients = 0;
        for (int i : ingredients.values()) {
            totalIngredients += i;
        }
        if (totalIngredients > 9) {
            throw new Recipes.RecipeException("Recipes can have at most 9 ingredients");
        }

        // Create Recipe
        recipe = new ShapelessRecipe(result);
        for (Ingredient ingredient : ingredients.keySet()) {
            recipe.addIngredient(ingredients.get(ingredient), ingredient.getMaterialData()); // TODO Ignoring item data
        }

        // Create GUIRecipe
        guiRecipe = new GUIRecipe(ingredients, result);
    }

    @Override
    public ItemStack getResult() {
        return result.clone();
    }

    @Override
    public ShapelessRecipe getRecipe() {
        return recipe;
    }

    @Override
    public GUIRecipe getGUIRecipe() {
        return guiRecipe;
    }

    @Override
    public boolean matchesMatrix(ItemStack[] matrix) {
        // Set all items in matrix to null, or set amount to 1
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
        if (!guiRecipe.removeIngredients(matrix)) return false;
        for (ItemStack is : matrix) {
            if (is != null) return false;
        }

        // Matches the matrix
        return true;
    }
}
