package co.kepler.fastcraftplus.recipes;

import co.kepler.fastcraftplus.util.BukkitUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

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
     * @param items The items to remove the ingredients from.
     * @return Returns true if the ingredients were all removed.
     */
    public boolean removeIngredients(ItemStack[] items, int multiplier) {
        int amount = getAmount() * multiplier;
        for (int i = items.length - 1; i >= 0 && amount > 0; i--) {
            ItemStack is = items[i];
            if (is == null || !matchesItem(is)) continue;
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

    /**
     * See if an ItemStack matches this ingredient.
     *
     * @param is The ItemStack to compare.
     * @return Returns true if the ItemStack can be used as this ingredient.
     */
    @SuppressWarnings("deprecation")
    public boolean matchesItem(ItemStack is) {
        if (is == null || is.getType() == Material.AIR)
            return getType() == Material.AIR;

        ItemStack compare = this;
        if (anyData()) {
            compare = super.clone();
            compare.setDurability(is.getDurability());
        }
        return compare.isSimilar(is);
    }
}
