package co.kepler.fastcraftplus.recipes;

import co.kepler.fastcraftplus.FastCraft;
import co.kepler.fastcraftplus.recipes.custom.CustomRecipe;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Listens to crafting events.
 */
public class CraftingListener implements Listener {

    @EventHandler
    public void onPrepareItemCraft(PrepareItemCraftEvent e) {
        for (CustomRecipe recipe : FastCraft.recipes().getRecipes()) { // TODO Hash for efficiency
            if (!RecipeUtil.areEqual(recipe.getRecipe(), e.getRecipe())) continue;
            if (recipe.matchesMatrix(e.getInventory().getMatrix())) {
                e.getInventory().setResult(recipe.getDisplayResult());
            } else {
                e.getInventory().setResult(null);
            }
            break;
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getSlot() < e.getInventory().getSize()) invInteract(e);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryDrag(InventoryDragEvent e) {
        invInteract(e);
    }

    /**
     * Trigger the PrepareItemCraftEvent in one tick when the crafting grid changes.
     *
     * @param e The interaction event.
     */
    public void invInteract(InventoryInteractEvent e) {
        final Inventory inv = e.getInventory();
        if (!e.isCancelled() && inv instanceof CraftingInventory) {
            new BukkitRunnable() {
                public void run() {
                    inv.setItem(1, inv.getItem(1));
                }
            }.runTask(FastCraft.getInstance());
        }
    }

    @EventHandler
    public void onCraftItem(CraftItemEvent e) {

    }
}
