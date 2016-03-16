package co.kepler.fastcraftplus.crafting;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

/**
 * An ingredient to an item recipe.
 */
public class Ingredient {
    private static final byte ANY_DATA = -1;

    private final MaterialData material;
    private final ItemMeta meta;

    /**
     * Create an ingredient from an item.
     * @param item The item to create an ingredient from.
     */
    public Ingredient(ItemStack item) {
        material = item.getData();
        meta = item.hasItemMeta() ? item.getItemMeta() : null;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof Ingredient)) {
            return false;
        }
        Ingredient ing = (Ingredient) o;
        if (!material.equals(ing.material)) return false;
        return Bukkit.getItemFactory().equals(meta, ing.meta);
    }

    /**
     * Get the material type of this ingredient.
     * @return Returns the material type of this item.
     */
    public Material getMaterial() {
        return material.getItemType();
    }

    /**
     * Create an ItemStack from this ingredient.
     * @param amount The number of items in the ItemStack.
     * @return Returns a new ItemStack.
     */
    public ItemStack toItemStack(int amount) {
        ItemStack result = material.toItemStack(amount);
        result.setItemMeta(meta);
        return result;
    }

    /**
     * See if an item equals this ingredient.
     * @param item The ItemStack to compare.
     * @return Returns true if the item equals this ingredient.
     */
    public boolean matchesItem(ItemStack item) {
        if (material.getItemType() != item.getType()) return false;
        if (material.getData() != ANY_DATA && material.getData() != item.getData().getData()) return false;
        return Bukkit.getItemFactory().equals(meta, item.getItemMeta());
    }
}
