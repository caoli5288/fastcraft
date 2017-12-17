package co.kepler.fastcraft.craftgui.layouts;

import co.kepler.fastcraft.FastCraft;
import co.kepler.fastcraft.craftgui.GUIFastCraft;
import co.kepler.fastcraft.recipes.FastRecipe;

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
