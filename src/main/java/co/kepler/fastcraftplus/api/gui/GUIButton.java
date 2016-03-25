package co.kepler.fastcraftplus.api.gui;

import org.bukkit.Sound;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 * A button for the GUI that players can interact with.
 */
public class GUIButton {
    private static Sound DEFAULT_CLICK_SOUND = null;

    private ItemStack item;
    private boolean visible = true;
    private ClickAction clickAction = null;

    /**
     * Create a new GUIButton with a null ItemStack
     */
    protected GUIButton() {
    }

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
     * Set the item that represents this button.
     */
    protected void setItem(ItemStack item) {
        this.item = item;
    }

    /**
     * See if this button is visible in the GUI.
     *
     * @return Returns true if the button should be visible in the GUI.
     */
    public boolean isVisible() {
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
        if (DEFAULT_CLICK_SOUND == null) {
            try {
                // 1.9
                DEFAULT_CLICK_SOUND = Sound.valueOf("UI_BUTTON_CLICK");
            } catch (IllegalArgumentException e) {
                // 1.8 and before
                DEFAULT_CLICK_SOUND = Sound.valueOf("CLICK");
            }
        }
        return DEFAULT_CLICK_SOUND;
    }

    /**
     * Called when the button is clicked.
     *
     * @param layout   The layout in which the button was clicked.
     * @param invEvent The inventory event triggered by the click.
     */
    public boolean onClick(Layout layout, InventoryClickEvent invEvent) {
        return clickAction != null && clickAction.onClick(new Click(this, invEvent));
    }

    /**
     * Set the action to be run on a button click. (Lambda friendly)
     *
     * @param clickAction The click action to be run when the button is clicked.
     */
    public void setClickAction(ClickAction clickAction) {
        this.clickAction = clickAction;
    }

    /**
     * Contains code to be run when a button is clicked.
     */
    public interface ClickAction {
        /**
         * Called when the button is clicked in the GUI.
         *
         * @param clickInfo The information about the button click.
         * @return Return true if the button's click noise should be played.
         */
        boolean onClick(Click clickInfo);
    }

    /**
     * Info about a button click that can be used in the ClickAction.
     */
    public class Click {
        public final InventoryClickEvent event;
        GUIButton button;

        public Click(GUIButton button, InventoryClickEvent event) {
            this.button = button;
            this.event = event;
        }
    }
}
