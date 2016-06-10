package co.kepler.fastcraftplus.recipes;

import co.kepler.fastcraftplus.FastCraft;
import co.kepler.fastcraftplus.recipes.custom.CustomRecipe;
import co.kepler.fastcraftplus.util.InvUtil;
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
        CustomRecipe recipe = FastCraft.recipes().getRecipe(e.getRecipe());
        if (recipe == null) return;
        boolean matches = recipe.matchesMatrix(e.getInventory().getMatrix());
        e.getInventory().setResult(matches ? recipe.getDisplayResult() : null);
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
        if (e.isCancelled() || !(inv instanceof CraftingInventory)) return;
        new BukkitRunnable() {
            public void run() {
                inv.setItem(1, inv.getItem(1)); // Triggers PrepareItemCraftEvent
            }
        }.runTask(FastCraft.getInstance());
    }

    /**
     * Cancels the craft event if it's a custom recipe that can't be crafted.
     *
     * @param e The CraftItemEvent.
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onCraftItemPre(CraftItemEvent e) {
        if (e.isCancelled()) return;

        if (InvUtil.shouldCancelCraftEvent(e)) {
            e.setCancelled(true);
            return;
        }

        CustomRecipe recipe = FastCraft.recipes().getRecipe(e.getRecipe());
        if (recipe == null) return;
        e.setCancelled(!recipe.matchesMatrix(e.getInventory().getMatrix()));
        return;
    }

    /**
     * Removes ingredients from the inventory.
     *
     * @param e The CraftItemEvent.
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCraftItemPost(CraftItemEvent e) {
        onCraftItemPre(e);
        if (e.isCancelled()) return;
        CustomRecipe recipe = FastCraft.recipes().getRecipe(e.getRecipe());
        if (recipe == null) return;
        recipe.removeFromMatrix(e);
    }
}
