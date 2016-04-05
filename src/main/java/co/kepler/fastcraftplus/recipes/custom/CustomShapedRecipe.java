package co.kepler.fastcraftplus.recipes.custom;

import co.kepler.fastcraftplus.config.Recipes;
import co.kepler.fastcraftplus.recipes.Ingredient;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A shaped recipe than supports ingredients with metadata.
 */
public class CustomShapedRecipe extends CustomRecipe {
    private final HashMap<Ingredient, Integer> ingredients;
    private final List<ItemStack> results;
    private final Ingredient[][] ingredientGrid;
    private final int rows, cols;
    private final ShapedRecipe recipe;
    private final ItemStack[] matrix;

    /**
     * Create a new instance of CustomShapedRecipe.
     *
     * @param result         The result of the recipe.
     * @param ingredientsMap The chars-ingredient map.
     * @param shape          The shape of the recipe.
     * @throws Recipes.RecipeException Thrown when recipe is improperly configured.
     */
    public CustomShapedRecipe(ItemStack result, Map<Character, Ingredient> ingredientsMap,
                              List<String> shape) throws Recipes.RecipeException {
        this.results = Collections.singletonList(result);

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
        ingredients = new HashMap<>();
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

        // Create matrix
        matrix = new ItemStack[9];
        for (int row = 0; row < shapeArr.length; row++) {
            char[] chars = shapeArr[row].toCharArray();
            for (int col = 0; col < chars.length; col++) {
                matrix[row * 3 + col] = ingredientsMap.get(chars[col]).toItemStack(1);
            }
        }
    }

    @Override
    public List<ItemStack> getResults() {
        return results;
    }

    @Override
    public ShapedRecipe getRecipe() {
        return recipe;
    }

    @Override
    public Map<Ingredient, Integer> getIngredients() {
        return ingredients;
    }

    @Override
    public ItemStack[] getMatrix() {
        return matrix;
    }

    @Override
    public boolean matchesMatrix(ItemStack[] matrix) {
        // Flip the matrix, so flipped recipes can be matched
        ItemStack[] matrixFlip = new ItemStack[matrix.length];
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                ItemStack curItem = matrix[x + (y * 3)];
                if (curItem != null && curItem.getType() == Material.AIR) {
                    matrix[x + (y * 3)] = null;
                }
                matrixFlip[(2 - x) + (y * 3)] = matrix[x + (y * 3)];
            }
        }

        // Compare the shape to the matrices
        for (int dx = 0; dx <= 3 - cols; dx++) {
            for (int dy = 0; dy <= 3 - rows; dy++) {
                if (matchesMatrix(matrix, dx, dy)) return true;
                if (matchesMatrix(matrixFlip, dx, dy)) return true;
            }
        }
        return false;
    }

    /**
     * Compare a matrix offset by a certain x and y.
     *
     * @param matrix The matrix to check.
     * @param dx     The x offset of this recipe in the grid.
     * @param dy     The y offset of this recipe in the grid.
     * @return Returns true if the matrix matches this recipe.
     */
    private boolean matchesMatrix(ItemStack[] matrix, int dx, int dy) {
        // Compare ingredients with items in the matrix
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                ItemStack item = matrix[row * 3 + col];
                if (row < dy || col < dx || row >= dy + rows || col >= dx + cols) {
                    // If outside the current recipe
                    if (item != null && item.getType() != Material.AIR) return false;
                } else {
                    // If inside the current recipe
                    Ingredient ingredient = ingredientGrid[row - dy][col - dx];
                    if (ingredient == null) {
                        if (item != null) return false;
                    } else {
                        if (!ingredient.matchesItem(item)) return false;
                    }
                }
            }
        }

        // None of the ingredients didn't match
        return true;
    }
}
