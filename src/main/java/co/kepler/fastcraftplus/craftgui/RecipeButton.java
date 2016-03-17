package co.kepler.fastcraftplus.craftgui;

import co.kepler.fastcraftplus.crafting.FastRecipe;
import co.kepler.fastcraftplus.gui.GUIButton;
import co.kepler.fastcraftplus.gui.GUILayout;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

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

    /**
     * Unsupported
     *
     * @param clickAction The click action to be run when the button is clicked.
     */
    @Override
    public void setClickAction(ButtonClickAction clickAction) {
        throw new UnsupportedOperationException();
    }

    /**
     * See if the button is visible.
     *
     * @return Returns true if the player's inventory has the necessary items to craft this recipe.
     */
    @Override
    public boolean isVisible() {
        return recipe.canCraft(gui.getPlayer(), false);
    }

    /**
     * Unsupported
     *
     * @param visible Returns true if the button is visible.
     */
    @Override
    public void setVisible(boolean visible) {
        throw new UnsupportedOperationException();
    }

    /**
     * Crafts the recipe associated with this button using the player's items.
     *
     * @param layout   The layout in which the button was clicked.
     * @param invEvent The inventory event triggered by the click.
     */
    @Override
    public void onClick(GUILayout layout, InventoryClickEvent invEvent) {
        if (!recipe.canCraft(gui.getPlayer(), true)) return;

        switch (invEvent.getClick()) {
            case LEFT:
            case SHIFT_LEFT:
            case RIGHT:
            case SHIFT_RIGHT:
                // Add to inventory. Drop rest on ground if not enough space.
                Inventory inv = gui.getPlayer().getInventory();
                for (ItemStack is : inv.addItem(recipe.getResults()).values()) {
                    invEvent.getView().setItem(InventoryView.OUTSIDE, is);
                }
                break;
            case DROP:
            case CONTROL_DROP:
                // Drop items on the ground.
                for (ItemStack is : recipe.getResults()) {
                    invEvent.getView().setItem(InventoryView.OUTSIDE, is);
                }
                break;
            default:
                break;
        }

    }


}
