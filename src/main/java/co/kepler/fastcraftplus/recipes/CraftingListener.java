package co.kepler.fastcraftplus.recipes;

import co.kepler.fastcraftplus.FastCraft;
import co.kepler.fastcraftplus.recipes.custom.CustomRecipe;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Listens to crafting events.
 */
public class CraftingListener implements Listener {

    @EventHandler
    public void onPrepareItemCraft(PrepareItemCraftEvent e) {
        System.out.println("A");
        for (CustomRecipe recipe : FastCraft.recipes().getRecipes()) { // TODO Hash for efficiency
            if (!RecipeUtil.areEqual(recipe.getRecipe(), e.getRecipe())) continue;
            e.getInventory().setResult(null);
            setResult(e.getInventory());
            break;
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClick(InventoryClickEvent e) {
        System.out.println("B");
        if (e.isCancelled()) return;
        inventoryChange(e.getInventory());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryDrag(InventoryDragEvent e) {
        System.out.println("C");
        if (e.isCancelled()) return;
        inventoryChange(e.getInventory());
    }

    /**
     * Called when an inventory is changed. Updates the item in the inventory.
     *
     * @param i The changed inventory.
     */
    private void inventoryChange(final Inventory i) {
        if (!(i instanceof CraftingInventory)) return;
        new BukkitRunnable() {
            public void run() {
                setResult((CraftingInventory) i);
            }
        }.runTask(FastCraft.getInstance());
    }

    /**
     * Set the result of the inventory to a custom recipe.
     *
     * @param inv The crafting inventory.
     */
    private void setResult(CraftingInventory inv) {
        ItemStack[] matrix = inv.getMatrix();
        for (CustomRecipe recipe : FastCraft.recipes().getRecipes()) {
            if (recipe.matchesMatrix(matrix)) {
                inv.setResult(recipe.getDisplayResult());
                updateInventory(inv);
                return;
            }
        }
    }

    @EventHandler
    public void onCraftItem(CraftItemEvent e) {

    }

    /**
     * Update the contents of an inventory for all players viewing it.
     *
     * @param inv The inventory to update.
     */
    @SuppressWarnings("deprecation")
    private void updateInventory(Inventory inv) {
        for (HumanEntity e : inv.getViewers()) {
            if (e instanceof Player) {
                ((Player) e).updateInventory();
            }
        }
    }
}
