package co.kepler.fastcraftplus.crafting;

import co.kepler.fastcraftplus.config.Recipes;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;

/**
 * Listens to crafting events.
 */
public class CraftingListener implements Listener {

    @EventHandler
    public void onPrepareItemCraft(PrepareItemCraftEvent e) {
        for (FCRecipe recipe : Recipes.getRecipes()) {
            if (!RecipeUtil.areEqual(recipe.getRecipe(), e.getRecipe())) continue;

            CraftingInventory inv = e.getInventory();
            if (!recipe.matchesMatrix(inv.getMatrix())) {
                inv.setResult(null);
            }
            break;
        }
    }
}
