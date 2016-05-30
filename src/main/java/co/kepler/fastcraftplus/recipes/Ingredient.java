package co.kepler.fastcraftplus.recipes;

import co.kepler.fastcraftplus.BukkitUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

/**
 * An ingredient to an item recipe.
 */
public class Ingredient extends ItemStack {
    private static final byte ANY_DATA = -1;

    /**
     * Create an ingredient from an item.
     *
     * @param item The item to create an ingredient from.
     */
    public Ingredient(ItemStack item) {
        super(item);
    }

    /**
     * See if any data can be used for this ingredient.
     *
     * @return Returns true if any data can be used.
     */
    @SuppressWarnings("deprecation")
    public boolean anyData() {
        return getData().getData() == ANY_DATA;
    }

    /**
     * Get the material data of this ingredient.
     *
     * @return Returns the material data of this ingredient.
     */
    public MaterialData getMaterialData() {
        return getData().clone();
    }

    /**
     * Get the material type of this ingredient.
     *
     * @return Returns the material type of this item.
     */
    public Material getMaterial() {
        return getData().getItemType();
    }

    /**
     * Get the name of the ingredient.
     *
     * @return Returns the ingredient's name.
     */
    public String getName() {
        return BukkitUtil.getItemName(this);
    }

    /**
     * Remove this ingredient from an inventory.
     *
     * @param items  The items to remove the ingredients from.
     * @return Returns true if the ingredients were all removed.
     */
    public boolean removeIngredients(ItemStack[] items) {
        int amount = getAmount();
        for (int i = items.length - 1; i >= 0 && amount > 0; i--) {
            ItemStack is = items[i];
            if (!isSimilar(is)) continue;
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

    @Override
    public Ingredient clone() {
        return new Ingredient(super.clone());
    }

    public Ingredient clone(int amount) {
        Ingredient result = clone();
        result.setAmount(amount);
        return result;
    }
}
