package co.kepler.fastcraftplus.craftgui;

import co.kepler.fastcraftplus.api.gui.LayoutPaged;
import co.kepler.fastcraftplus.recipes.FastRecipe;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * A paged GUI layout that shows craftable recipes.
 */
public abstract class LayoutRecipes extends LayoutPaged {
    private final List<FastRecipe> activeRecipes;
    private final GUIFastCraft gui;

    public LayoutRecipes(GUIFastCraft gui) {
        this.gui = gui;
        activeRecipes = new ArrayList<>();
    }

    /**
     * Update the recipes shown in the GUI.
     */
    public abstract void updateRecipes();

    /**
     * Adds all recipes from the given list that can be crafted, and are not already in the GUI.
     *
     * @param recipes The recipes to add to the GUI.
     */
    protected void addRecipes(List<FastRecipe> recipes) {
        for (FastRecipe r : recipes) {
            // If the button is already in the gui, or if it can't be crafted, continue.
            ItemStack[] contents = gui.getPlayer().getInventory().getContents();
            if (activeRecipes.contains(r) || !r.removeIngredients(contents)) continue;

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

    /**
     * Get the FastCraft GUI this layout is being used in.
     *
     * @return Return this layout's FastCraft GUI.
     */
    protected GUIFastCraft getGUI() {
        return gui;
    }
}
