package co.kepler.fastcraftplus.recipes;

import co.kepler.fastcraftplus.craftgui.GUIFastCraft;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Holds the ingredients and result of a recipe.
 */
public class GUIRecipe implements Comparable<GUIRecipe> {
    private Map<Ingredient, Integer> ingredients;
    private Recipe recipe;
    private ItemStack result;
    private ItemStack[] matrix;

    /**
     * Create a recipe from ingredients, a result, and byproducts.
     *
     * @param ingredients The ingredients used to craft this recipe.
     * @param result      The result of this recipe.
     */
    public GUIRecipe(Map<Ingredient, Integer> ingredients, Recipe recipe, ItemStack result, ItemStack[] matrix) {
        this.ingredients = ingredients;
        this.recipe = recipe;
        this.result = result;
        this.matrix = matrix;
    }

    /**
     * Create a new GUIRecipe from an existing recipe.
     *
     * @param recipe The recipe this GUIRecipe will be based off.
     */
    public GUIRecipe(Recipe recipe) {
        assert canBeGUIRecipe(recipe) : "Recipe must be a shaped or shapeless recipe";

        ingredients = new HashMap<>();
        this.recipe = recipe;
        result = recipe.getResult();

        // Get ingredients from recipe
        matrix = new ItemStack[9];
        int matIndex = 0;
        if (recipe instanceof ShapedRecipe) {
            ShapedRecipe r = (ShapedRecipe) recipe;
            for (String str : r.getShape()) {
                int matRow = 0;
                for (char c : str.toCharArray()) {
                    // Add ingredients to the ingredients map
                    ItemStack is = r.getIngredientMap().get(c);
                    if (is == null) continue;
                    Ingredient i = new Ingredient(is);
                    Integer amount = ingredients.get(i);
                    ingredients.put(i, (amount == null ? 0 : amount) + 1);

                    ItemStack matItem = is.clone();
                    matItem.setAmount(1);
                    matrix[matIndex + matRow] = matItem;
                }
                matIndex += 3;
            }
        } else if (recipe instanceof ShapelessRecipe) {
            ShapelessRecipe r = (ShapelessRecipe) recipe;
            for (ItemStack is : r.getIngredientList()) {
                // Add ingredient to the ingredients map
                Ingredient ingredient = new Ingredient(is);
                Integer amount = ingredients.get(ingredient);
                amount = (amount == null ? 0 : amount) + 1;
                ingredients.put(ingredient, amount);

                // Add items to the matrix
                ItemStack matItem = is.clone();
                matItem.setAmount(1);
                for (int i = 0; i < amount; i++) {
                    matrix[matIndex++] = matItem;
                }
            }
        }
    }

    /**
     * See if a recipe can be a GUIRecipe.
     *
     * @param recipe The recipe to check.
     * @return Return true if the recipe can be a GUI recipe.
     */
    public static boolean canBeGUIRecipe(Recipe recipe) {
        return (recipe != null) && (recipe instanceof ShapedRecipe || recipe instanceof ShapelessRecipe);
    }

    /**
     * Get the result of the recipe.
     *
     * @return The item crafted by this recipe.
     */
    public ItemStack getResult() {
        return result;
    }

    /**
     * Get the result from crafting this recipe in a crafting grid.
     *
     * @param gui The GUI containing this recipe.
     * @return Returns the result from crafting this recipe in a crafting grid.
     */
    public ItemStack getCraftingResult(GUIFastCraft gui) {
        CraftingInventory inv = gui.getCraftingInventory(recipe, matrix, result);
        PrepareItemCraftEvent prepareEvent = new PrepareItemCraftEvent(inv, gui.getCraftingInventoryView(), false);
        Bukkit.getPluginManager().callEvent(prepareEvent);
        return prepareEvent.getInventory().getResult();
    }

    /**
     * Gets all the results of this recipe, including byproducts
     * like empty buckets from recipes that require filled buckets.
     *
     * @param craftingResult The result from crafting in a crafting table.
     * @param results        The list to populate with crafting results.
     */
    private void getResults(ItemStack craftingResult, List<ItemStack> results) {
        results.clear();
        results.add(craftingResult);
        for (Ingredient i : ingredients.keySet()) {
            switch (i.getMaterial()) {
                case LAVA_BUCKET:
                case MILK_BUCKET:
                case WATER_BUCKET:
                    results.add(new ItemStack(Material.BUCKET, ingredients.get(i)));
            }
        }
    }

    /**
     * Get this recipe's ingredients.
     *
     * @return Returns a map of this recipe's ingredients, with the key being the
     * ingredient, and the value being the amount of the ingredient.
     */
    public Map<Ingredient, Integer> getIngredients() {
        return ingredients;
    }

    /**
     * Get the matrix of items in a crafting grid for this recipe.
     *
     * @return Returns the recipe's matrix.
     */
    public ItemStack[] getMatrix() {
        ItemStack[] copy = new ItemStack[matrix.length];
        for (int i = 0; i < copy.length; i++) {
            copy[i] = matrix[i] == null ? null : matrix[i].clone();
        }
        return copy;
    }

    /**
     * Remove ingredients from an inventory.
     *
     * @param items The items to remove the ingredients from.
     * @return Returns true if the inventory had the necessary ingredients.
     */
    public boolean removeIngredients(ItemStack[] items) {
        LinkedList<Ingredient> toRemove = new LinkedList<>();

        // Add ingredients. Those that can use any data go at the end.
        for (Ingredient i : ingredients.keySet()) {
            if (i.anyData()) {
                toRemove.addLast(i);
            } else {
                toRemove.addFirst(i);
            }
        }

        // Remove ingredients.
        for (Ingredient i : toRemove) {
            if (!i.removeIngredients(items, ingredients.get(i))) {
                // If unable to remove all of this ingredient
                return false;
            }
        }

        return true;
    }

    /**
     * See if a player has this recipe's ingredients, and optionally, remove them
     * from the player's inventory if all ingredients are present.
     *
     * @param gui   The GUI this recipe is being crafted in.
     * @param craft Whether this recipe should be crafted if the player has all the ingredients.
     * @return Returns true if the ingredients were removed from the player's inventory.
     */
    public boolean canCraft(GUIFastCraft gui, boolean craft, List<ItemStack> results) {
        Player player = gui.getPlayer();
        ItemStack craftingResult = getCraftingResult(gui);
        if (craftingResult == null || craftingResult.getType() == Material.AIR) return false;

        // Clone the items in the player's inventory
        ItemStack[] contents = player.getInventory().getContents();
        for (int i = 0; i < contents.length; i++) {
            if (contents[i] == null) continue;
            contents[i] = contents[i].clone();
        }

        boolean allRemoved = removeIngredients(contents);
        if (allRemoved && craft) {
            // Call crafting event. Cancel crafting if event is cancelled.
            CraftItemEvent craftEvent = new CraftItemEvent(recipe, gui.getCraftingInventoryView(),
                    InventoryType.SlotType.RESULT, 0, ClickType.LEFT, InventoryAction.PICKUP_ONE);
            Bukkit.getPluginManager().callEvent(craftEvent);
            if (craftEvent.isCancelled() || craftEvent.getResult() == Event.Result.DENY) return false;

            // Update inventory to reflect removed items
            player.getInventory().setContents(contents);
            getResults(craftingResult, results);

            // Award achievement
            RecipeUtil.getInstance().awardAchievement(player, craftingResult);
        }
        return allRemoved;
    }

    /**
     * See if a player has this recipe's ingredients, and optionally, remove them
     * from the player's inventory if all ingredients are present.
     *
     * @param gui The GUI this recipe is being crafted in.
     */
    public boolean canCraft(GUIFastCraft gui) {
        return canCraft(gui, false, null);
    }

    @Override
    @SuppressWarnings("deprecation")
    public int compareTo(GUIRecipe compareTo) {
        int i = result.getTypeId() - compareTo.result.getTypeId();
        if (i != 0) return i;

        i = result.getData().getData() - compareTo.result.getData().getData();
        if (i != 0) return i;

        return result.getAmount() - compareTo.result.getAmount();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o == null || !(o instanceof GUIRecipe)) return false;

        GUIRecipe r = (GUIRecipe) o;
        return result.equals(r.result) && ingredients.equals(r.ingredients);
    }

    @Override
    public int hashCode() {
        int hash = ingredients.hashCode();
        return 31 * hash + result.hashCode();
    }
}
