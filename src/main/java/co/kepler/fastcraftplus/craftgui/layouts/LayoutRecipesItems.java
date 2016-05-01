package co.kepler.fastcraftplus.craftgui.layouts;

import co.kepler.fastcraftplus.FastCraft;
import co.kepler.fastcraftplus.craftgui.GUIFastCraft;
import co.kepler.fastcraftplus.recipes.FastRecipe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A recipes layout that shows item crafting recipes.
 */
public class LayoutRecipesItems extends LayoutRecipes {
    private final List<FastRecipe> allRecipes;

    public LayoutRecipesItems(GUIFastCraft gui) {
        super(gui);

        allRecipes = new ArrayList<>();
        for (FastRecipe recipe : FastCraft.recipeManager().getRecipes(gui.getPlayer())) {
            if (FastCraft.blacklist().isAllowed(recipe, gui.getPlayer())) {
                allRecipes.add(recipe);
            }
        }
        Collections.sort(allRecipes);
    }

    @Override
    public void updateRecipes() {
        this.addRecipes(allRecipes);
    }
}
