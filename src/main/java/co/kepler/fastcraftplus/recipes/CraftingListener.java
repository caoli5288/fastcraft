package co.kepler.fastcraftplus.recipes;

import co.kepler.fastcraftplus.FastCraft;
import co.kepler.fastcraftplus.recipes.custom.CustomRecipe;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;

/**
 * Listens to crafting events.
 */
public class CraftingListener implements Listener {

    @EventHandler
    public void onPrepareItemCraft(PrepareItemCraftEvent e) {
        for (CustomRecipe recipe : FastCraft.recipes().getRecipes()) { // TODO Hash for efficiency
            if (!RecipeUtil.areEqual(recipe.getRecipe(), e.getRecipe())) continue;
            e.getInventory().setResult(null);
            setResult(e.getInventory());
            break;
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getInventory() instanceof CraftingInventory) {
            setResult((CraftingInventory) e.getInventory());
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent e) {
        if (e.getInventory() instanceof CraftingInventory) {
            setResult((CraftingInventory) e.getInventory());
        }
    }

    /**
     * Set the result of the inventory to a custom recipe.
     *
     * @param inv The crafting inventory.
     */
    private void setResult(CraftingInventory inv) {
        ItemStack[] matrix = inv.getMatrix();
        for (CustomRecipe recipe : FastCraft.recipes().getRecipes()) {
            if (!recipe.matchesMatrix(matrix)) continue;
            inv.setResult(recipe.getDisplayResult());
            return;
        }
    }

    @EventHandler
    public void onCraftItem(CraftItemEvent e) {

    }
}
