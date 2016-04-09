package co.kepler.fastcraftplus.recipes;

import co.kepler.fastcraftplus.FastCraft;
import co.kepler.fastcraftplus.config.RecipesConfig;
import co.kepler.fastcraftplus.recipes.custom.CustomRecipe;
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
        for (CustomRecipe recipe : FastCraft.recipes().getRecipes()) {
            if (!RecipeUtil.areEqual(recipe.getRecipe(), e.getRecipe())) continue;

            CraftingInventory inv = e.getInventory();
            if (!recipe.matchesMatrix(inv.getMatrix())) {
                inv.setResult(null);
            }
            break;
        }
    }
}
