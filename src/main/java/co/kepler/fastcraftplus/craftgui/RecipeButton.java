package co.kepler.fastcraftplus.craftgui;

import co.kepler.fastcraftplus.crafting.FastRecipe;
import co.kepler.fastcraftplus.gui.GUIButton;
import co.kepler.fastcraftplus.gui.GUILayout;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * A button that will encapsulate a crafting recipe.
 */
public class RecipeButton extends GUIButton {
    FastRecipe recipe;
    private GUIFastCraft gui;

    /**
     * Create a new Recipe Button from a GUI and a FastRecipe.
     *
     * @param gui    The FastCraft GUI that this button is contained in.
     * @param recipe The recipe that this button will craft.
     */
    public RecipeButton(GUIFastCraft gui, FastRecipe recipe) {
        super(recipe.getResult());
        this.gui = gui;
        this.recipe = recipe;
    }

    @Override
    public void setClickAction(ButtonClickAction clickAction) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void onClick(GUILayout layout, InventoryClickEvent invEvent) {
        if (!(layout instanceof GUILayoutCrafting)) return;

        GUILayoutCrafting craftingLayout = (GUILayoutCrafting) layout;

    }


}
