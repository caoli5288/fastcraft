package co.kepler.fastcraftplus.recipes;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * A recipe that will be used by the FastCraft+ user interface.
 */
public abstract class FastRecipe implements Comparable<FastRecipe> {

    /**
     * Get the ingredients required to craft this recipe.
     *
     * @return Returns the ingredients required to craft this recipe.
     */
    public abstract Map<Ingredient, Integer> getIngredients();

    /**
     * Get the results of this recipe.
     *
     * @return Returns the results of this recipe.
     */
    public abstract List<ItemStack> getResults();

    /**
     * Get the item shown in the FastCraft+ interface. Returns the first
     * result in getResults() by default.
     *
     * @return Returns the result shown in the FastCraft+ interface.
     */
    public ItemStack getDisplayResult() {
        List<ItemStack> results = getResults();
        return results.isEmpty() ? null : results.get(0);
    }

    /**
     * Get the byproducts of this recipe. By default, returns one empty bucket
     * for every non-empty bucket used in the recipe.
     *
     * @return Returns the results of this recipe.
     */
    public Set<ItemStack> getByproducts() {
        Set<ItemStack> result = new HashSet<>();

        // Count the number of buckets to be returned
        int buckets = 0;
        for (Ingredient i : getIngredients().keySet()) {
            switch (i.getMaterial()) {
                case LAVA_BUCKET:
                case MILK_BUCKET:
                case WATER_BUCKET:
                    buckets += getIngredients().get(i);
            }
        }

        // Add buckets to the result
        int stackSize = Material.BUCKET.getMaxStackSize();
        while (buckets > stackSize) {
            result.add(new ItemStack(Material.BUCKET, stackSize));
            buckets -= stackSize;
        }
        if (buckets > 0) {
            result.add(new ItemStack(Material.BUCKET, buckets));
        }

        // Return the list of byproducts
        return result;
    }

    /**
     * Get this recipes results, including its main result, and its byproducts.
     *
     * @return Returns this recipe's results.
     */
    public final Set<ItemStack> getAllResults() {
        Set<ItemStack> items = new HashSet<>();
        items.addAll(getResults());
        items.addAll(getByproducts());
        return items;
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
        Map<Ingredient, Integer> ingredients = getIngredients();
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
     * @param player The player crafting this recipe.
     * @param remove If a player has all the necessary items, remove them.
     * @return Returns true if the ingredients were removed from the player's inventory.
     */
    public boolean canCraftFromItems(Player player, boolean remove) {
        // Clone the items in the player's inventory
        ItemStack[] contents = player.getInventory().getContents();
        for (int i = 0; i < contents.length; i++) {
            if (contents[i] == null) continue;
            contents[i] = contents[i].clone();
        }

        // Remove items, and see if the player has all of them
        boolean hasAll = removeIngredients(contents);

        // If all items were removed, update the player's inventory
        if (hasAll && remove) {
            player.getInventory().setContents(contents);
            return true;
        }

        // Return whether the player had all the items
        return hasAll;
    }

    /**
     * Craft this recipe, and get the recipe's results.
     *
     * @param player The player crafting the recipe.
     * @return Returns a set of resulting items, or null if the crafting was unsuccessful.
     */
    public Set<ItemStack> craft(Player player) {
        if (!canCraftFromItems(player, true)) return null;

        for (ItemStack is : getResults()) {
            RecipeUtil.awardAchievement(player, is);
        }
        return getAllResults();
    }

    @Override
    @SuppressWarnings("deprecation")
    public int compareTo(FastRecipe compareTo) {
        ItemStack result = getDisplayResult();
        ItemStack compResult = compareTo.getDisplayResult();

        // Compare item ID's
        int i = result.getTypeId() - compResult.getTypeId();
        if (i != 0) return i;

        // Compare item data
        i = result.getData().getData() - compResult.getData().getData();
        if (i != 0) return i;

        // Compare amounts
        return result.getAmount() - compResult.getAmount();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof FastRecipe)) return false;

        FastRecipe fr = (FastRecipe) o;
        if (!getResults().equals(fr.getResults())) return false;
        if (!getDisplayResult().equals(fr.getDisplayResult())) return false;
        if (!getIngredients().equals(fr.getIngredients())) return false;
        return getByproducts().equals(fr.getByproducts());
    }

    @Override
    public int hashCode() {
        int hash = getResults().hashCode();
        hash = hash * 31 + getDisplayResult().hashCode();
        hash = hash * 31 + getIngredients().hashCode();
        return hash * 31 + getByproducts().hashCode();
    }
}
