package co.kepler.fastcraftplus.util;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * A utility class for inventories.
 */
public class InvUtil {

    /**
     * See if there is enough space in an inventory for an item to be added.
     *
     * @param inv  The inventory to check.
     * @param item The item to add.
     * @return Returns true if there is enough space in the inventory to add the item.
     */
    public static boolean canAddItem(Inventory inv, ItemStack item) {
        int amount = item.getAmount();
        int max = item.getMaxStackSize();
        for (ItemStack is : inv) {
            if (is == null || is.getType() == Material.AIR) {
                return true;
            } else if (is.isSimilar(item) && is.getAmount() < max) {
                amount -= max - is.getAmount();
            }
            if (amount <= 0) return true;
        }
        return false;
    }

    /**
     * Add an item to a player's inventory as if it were shift-clicked.
     *
     * @param inv The inventory to add the item to.
     * @param item The item to add to the inventory.
     */
    public static void shiftAddToPlayerInv(Inventory inv, ItemStack item) {
        // Add inventory index order: 8 -> 0, (size - 1) -> 9
        int toAdd = item.getAmount();
        int maxSize = item.getMaxStackSize();
        ItemStack[] contents = inv.getContents();
        for (int i = contents.length + 8; i > 8 && toAdd > 0; i--) {
            int index = i % contents.length;
            ItemStack curItem = contents[index];
            if (curItem == null || curItem.getType() == Material.AIR) {
                ItemStack newItem = item.clone();
                newItem.setAmount(toAdd);
                contents[index] = newItem;
                toAdd = 0;
            } else if (item.isSimilar(curItem) && curItem.getAmount() < maxSize) {
                int add = Math.min(maxSize - curItem.getAmount(), toAdd);
                curItem.setAmount(curItem.getAmount() + add);
                toAdd -= add;
            }
        }
    }
}
