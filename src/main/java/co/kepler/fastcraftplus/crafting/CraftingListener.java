package co.kepler.fastcraftplus.crafting;

import co.kepler.fastcraftplus.config.Recipes;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Listens to crafting events.
 */
public class CraftingListener implements Listener {

    @EventHandler
    public void onPrepareItemCraft(PrepareItemCraftEvent e) {
        ItemStack[] matrix = e.getInventory().getMatrix();
        for (FCRecipe recipe : Recipes.getRecipes()) {
            if (recipe.matchesMatrix(matrix)) {
                e.getInventory().setResult(recipe.getResult());
                return;
            }
        }
    }
}
