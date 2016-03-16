package co.kepler.fastcraftplus.gui;

import org.bukkit.Sound;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 * A button for the GUI that players can interact with.
 */
public class GUIButton {
    private ItemStack item;
    private boolean visible = true;
    private ButtonClickAction clickAction = null;

    /**
     * Create a new GUIButton from an ItemStack.
     *
     * @param item The item that represents the button.
     */
    public GUIButton(ItemStack item) {
        this.item = item;
    }

    /**
     * Create a new GUIButton that copies another.
     *
     * @param copy The button that this new button will be based off of.
     */
    public GUIButton(GUIButton copy) {
        item = new ItemStack(copy.getItem());
        clickAction = copy.clickAction;
    }

    /**
     * Get the item that represents this button.
     *
     * @return Returns the item that represents this button.
     */
    public ItemStack getItem() {
        return item;
    }

    /**
     * See if this button is visible in the GUI.
     *
     * @return Returns true if the button should be visible in the GUI.
     */
    public boolean isVisible(GUILayout layout) {
        return visible;
    }

    /**
     * Set the visibility of the button.
     *
     * @param visible Returns true if the button is visible.
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    /**
     * Get the sound to be played when the button is clicked
     */
    public Sound getClickSound() {
        return Sound.UI_BUTTON_CLICK;
    }

    /**
     * Called when the button is clicked.
     *
     * @param layout   The layout in which the button was clicked.
     * @param invEvent The inventory event triggered by the click.
     */
    public void onClick(GUILayout layout, InventoryClickEvent invEvent) {
        if (clickAction == null) return;
        clickAction.onClick(new ButtonClick(this, invEvent));
    }

    /**
     * Set the action to be run on a button click. (Lambda friendly)
     *
     * @param clickAction The click action to be run when the button is clicked.
     */
    public void setClickAction(ButtonClickAction clickAction) {
        this.clickAction = clickAction;
    }

    /**
     * Contains code to be run when a button is clicked.
     */
    public interface ButtonClickAction {
        void onClick(ButtonClick clickInfo);
    }

    /**
     * Info about a button click that can be used in the ButtonClickAction.
     */
    public class ButtonClick {
        public final InventoryClickEvent event;
        GUIButton button;

        public ButtonClick(GUIButton button, InventoryClickEvent event) {
            this.button = button;
            this.event = event;
        }
    }
}
