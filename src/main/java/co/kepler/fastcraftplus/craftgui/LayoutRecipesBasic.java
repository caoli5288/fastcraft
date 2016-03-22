package co.kepler.fastcraftplus.craftgui;

import co.kepler.fastcraftplus.crafting.RecipeUtil;

/**
 * A recipes layout that shows basic crafting recipes.
 */
public class LayoutRecipesBasic extends LayoutRecipes {

    public LayoutRecipesBasic(GUIFastCraft gui) {
        super(gui);
    }

    @Override
    public void updateRecipes() {
        addRecipes(RecipeUtil.getRecipes());
    }
}
