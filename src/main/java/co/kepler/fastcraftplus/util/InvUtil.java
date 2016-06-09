package co.kepler.fastcraftplus.util;

import org.bukkit.Material;
import org.bukkit.event.inventory.CraftItemEvent;
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
     * @param inv  The inventory to add the item to.
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

    /**
     * See if a CraftItemEvent should be cancelled. The event is called when the
     * recipe won't be crafted anyway, like if a player clicks on the result with
     * a different item on the cursor.
     *
     * @param e The CraftItemEvent.
     * @return Returns true if the event should be cancelled.
     */
    public static boolean shouldCancelCraftEvent(CraftItemEvent e) {
        Inventory bottom = e.getView().getBottomInventory();
        ItemStack result = e.getInventory().getResult();
        ItemStack cursor = e.getCursor();

        if (result == null || result.getType() == Material.AIR) return true;
        switch (e.getClick()) {
        case LEFT:
        case RIGHT:
            if (cursor == null || cursor.getType() == Material.AIR) return false;
            if (!result.isSimilar(e.getCursor())) return true;
            return result.getAmount() + e.getCursor().getAmount() > result.getMaxStackSize();
        case SHIFT_RIGHT:
        case SHIFT_LEFT:
            return !canAddItem(bottom, result);
        case NUMBER_KEY:
            // Target slot must be empty
            ItemStack item = bottom.getContents()[e.getHotbarButton()];
            return item != null && item.getType() != Material.AIR;
        case DOUBLE_CLICK:
            return true;
        case MIDDLE:
        case DROP:
        case CONTROL_DROP:
        case CREATIVE:
        case WINDOW_BORDER_LEFT:
        case WINDOW_BORDER_RIGHT:
        case UNKNOWN:
        }
        return false;
    }
}
