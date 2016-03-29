package co.kepler.fastcraftplus.crafting;

import co.kepler.fastcraftplus.config.Recipes;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A shaped recipe than supports ingredients with metadata.
 */
public class FCShapedRecipe implements FCRecipe {
    private final ItemStack result;
    private final Ingredient[][] ingredientGrid;
    private final int rows, cols;
    private final ShapedRecipe recipe;
    private final GUIRecipe guiRecipe;

    /**
     * Create a new instance of FCShapedRecipe.
     *
     * @param result         The result of the recipe.
     * @param ingredientsMap The chars-ingredient map.
     * @param shape          The shape of the recipe.
     * @throws Recipes.RecipeException Thrown when recipe is improperly configured.
     */
    public FCShapedRecipe(ItemStack result, Map<Character, Ingredient> ingredientsMap,
                          List<String> shape) throws Recipes.RecipeException {
        this.result = result;

        // Get the number of rows and columns in the shape.
        rows = shape.size();
        if (rows < 1 || rows > 3) throw new Recipes.RecipeException("The recipe's shape height must be from 1 to 3");
        cols = shape.get(0).length();
        if (cols < 1 || cols > 3) throw new Recipes.RecipeException("The recipe's shape width must be from 1 to 3");

        // Ensure all rows are the same width
        for (String s : shape) {
            if (s.length() != cols) throw new Recipes.RecipeException("All rows in the shape must be the same width");
        }

        // Copy ingredients to the matrix
        Map<Ingredient, Integer> ingredients = new HashMap<>();
        ingredientGrid = new Ingredient[rows][cols];
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                Ingredient ingredient = ingredientsMap.get(shape.get(row).charAt(col));
                Integer curAmount = ingredients.get(ingredient);
                ingredients.put(ingredient, (curAmount == null ? 0 : curAmount) + 1);
                ingredientGrid[row][col] = ingredient;
            }
        }

        // Create Recipe
        recipe = new ShapedRecipe(result);
        String[] shapeArr = new String[shape.size()];
        recipe.shape(shape.toArray(shapeArr));
        for (Character key : ingredientsMap.keySet()) {
            recipe.setIngredient(key, ingredientsMap.get(key).getMaterialData());
        }

        // Create GUIRecipe
        guiRecipe = new GUIRecipe(ingredients, result);
    }

    @Override
    public ItemStack getResult() {
        return result.clone();
    }

    @Override
    public ShapedRecipe getRecipe() {
        return recipe;
    }

    @Override
    public GUIRecipe getGUIRecipe() {
        return guiRecipe;
    }

    @Override
    public boolean matchesMatrix(ItemStack[] matrix) {
        for (int dx = 0; dx <= 3 - cols; dx++) {
            for (int dy = 0; dy <= 3 - rows; dy++) {
                if (matchesMatrix(matrix, dx, dy)) return true;
            }
        }
        return false;
    }

    /**
     * Compare a matrix offset by a certain x and y.
     *
     * @param matrix The matrix to check.
     * @param dx     The x offset.
     * @param dy     The y offset.
     * @return Returns true if the matrix matches this recipe.
     */
    private boolean matchesMatrix(ItemStack[] matrix, int dx, int dy) {
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                ItemStack item = matrix[row * 3 + col];
                if (row < dy || col < dx || row > dy + rows || col > dx + cols) {
                    if (item != null) return false;
                }
                Ingredient ingredient = ingredientGrid[row + dy][col + dx]; // TODO Finish coding
            }
        }
        // None of the ingredients didn't match
        return true;
    }
}
