package co.kepler.fastcraftplus.craftgui;

import co.kepler.fastcraftplus.FastCraft;
import co.kepler.fastcraftplus.recipes.RecipeUtil;

/**
 * A recipes layout that shows item crafting recipes.
 */
public class LayoutRecipesItems extends LayoutRecipes {

    public LayoutRecipesItems(GUIFastCraft gui) {
        super(gui);
    }

    @Override
    public void updateRecipes() {
        FastCraft.recipeManager().getRecipes(getGUI().getPlayer());
    }
}
