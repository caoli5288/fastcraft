package co.kepler.fastcraftplus.crafting;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import java.util.HashMap;
import java.util.Map;

/**
 * An ingredient to an item recipe.
 */
public class Ingredient {
    private static final byte ANY_DATA = -1;

    private final MaterialData material;
    private final String name;

    /**
     * Create an ingredient from an item.
     *
     * @param item The item to create an ingredient from.
     */
    public Ingredient(ItemStack item) {
        material = item.getData();
        name = RecipeUtil.getInstance().getItemName(item);
    }

    /**
     * Get a Map of ingredients, with the map's values being the number of the ingredient.
     *
     * @param items The items to convert to ingredients.
     * @return Returns a Map of ingredients and amounts.
     */
    public static Map<Ingredient, Integer> fromItems(ItemStack... items) {
        Map<Ingredient, Integer> result = new HashMap<>();
        for (ItemStack is : items) {
            if (is == null || is.getType() == Material.AIR) continue;
            Ingredient i = new Ingredient(is);
            Integer old = result.get(i);
            result.put(i, (old == null ? 0 : old) + 1);
        }
        return result;
    }

    /**
     * See if any data can be used for this ingredient.
     *
     * @return Returns true if any data can be used.
     */
    @SuppressWarnings("deprecation")
    public boolean anyData() {
        return material.getData() == ANY_DATA;
    }

    /**
     * Get the material type of this ingredient.
     *
     * @return Returns the material type of this item.
     */
    public Material getMaterial() {
        return material.getItemType();
    }

    /**
     * Get the name of the ingredient.
     *
     * @return Returns the ingredient's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Create an ItemStack from this ingredient.
     *
     * @param amount The number of items in the ItemStack.
     * @return Returns a new ItemStack.
     */
    public ItemStack toItemStack(int amount) {
        return material.toItemStack(amount);
    }

    /**
     * Remove this ingredient from an inventory.
     *
     * @param items  The items to remove the ingredients from.
     * @param amount The number of ingredients to remove.
     * @return Returns true if the ingredients were all removed.
     */
    public boolean removeIngredients(ItemStack[] items, int amount) {
        for (int i = items.length - 1; i >= 0 && amount > 0; i--) {
            ItemStack is = items[i];
            if (!matchesItem(is)) continue;
            if (amount >= is.getAmount()) {
                amount -= is.getAmount();
                items[i] = null;
            } else {
                items[i].setAmount(items[i].getAmount() - amount);
                amount = 0;
            }
        }
        return amount == 0;
    }

    /**
     * See if an ItemStack matches this ingredient. Will not match items with
     * metadata, so custom, possibly valuable, items aren't used accidentally.
     *
     * @param is The ItemStack to compare.
     * @return Returns true if the ItemStack can be used as this ingredient.
     */
    @SuppressWarnings("deprecation")
    public boolean matchesItem(ItemStack is) {
        if (is == null || is.hasItemMeta()) return false;
        MaterialData md = is.getData();
        if (material.getItemType() != md.getItemType()) return false;
        if (material.getData() == ANY_DATA || md.getData() == ANY_DATA) return true;
        return material.getData() == md.getData();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof Ingredient)) return false;
        return material.equals(((Ingredient) o).material);
    }

    @Override
    public int hashCode() {
        return material.hashCode();
    }
}
