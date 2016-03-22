package co.kepler.fastcraftplus.craftgui;

import co.kepler.fastcraftplus.crafting.FastRecipe;
import co.kepler.fastcraftplus.gui.LayoutPaged;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A paged GUI layout that shows craftable recipes.
 */
public class LayoutRecipes extends LayoutPaged {
    private final Set<FastRecipe> activeRecipes;
    private final GUIFastCraft gui;

    public LayoutRecipes(GUIFastCraft gui) {
        this.gui = gui;
        activeRecipes = new HashSet<>();
    }

    /**
     * Adds all recipes from the given list that can be crafted, and are not already in the GUI.
     *
     * @param recipes The recipes to add to the GUI.
     */
    public void addRecipes(List<FastRecipe> recipes) {
        for (FastRecipe r : recipes) {
            // If the button is already in the gui, or if it can't be crafted, continue.
            if (activeRecipes.contains(r) || !r.canCraft(gui.getPlayer(), false)) continue;

            // Create the button, and add it to the GUI.
            GUIButtonRecipe button = new GUIButtonRecipe(gui, r);
            setButton(activeRecipes.size(), button);
            activeRecipes.add(r);
        }
    }

    @Override
    public void clearButtons() {
        super.clearButtons();
        activeRecipes.clear();
    }
}
