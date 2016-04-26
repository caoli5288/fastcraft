package co.kepler.fastcraftplus.api.gui;

import org.bukkit.Sound;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 * A button for the GUI that players can interact with.
 */
public class GUIButton extends GUIButtonAbstract {
    private static Sound DEFAULT_CLICK_SOUND = null;

    private ItemStack item;
    private boolean visible = true;
    private ClickAction clickAction = null;

    /**
     * Create a new GUIButton.
     *
     * @param item        The item representing the button.
     * @param visible     Whether the button is visible in the interface.
     * @param clickAction The button's click action.
     */
    public GUIButton(ItemStack item, boolean visible, ClickAction clickAction) {
        this.item = item;
        this.visible = visible;
        this.clickAction = clickAction;
    }

    /**
     * Create a new GUIButton from an ItemStack.
     *
     * @param item The item that represents the button.
     */
    public GUIButton(ItemStack item) {
        this.item = item;
    }

    @Override
    public ItemStack getItem() {
        return item;
    }

    /**
     * Set the item that represents this button.
     */
    protected void setItem(ItemStack item) {
        this.item = item;
    }

    @Override
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

    @Override
    public boolean onClick(GUI gui, InventoryClickEvent invEvent) {
        return clickAction != null && clickAction.onClick(new ClickAction.Click(this, invEvent));
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

        /**
         * Info about a button click that can be used in the ClickAction.
         */
        class Click {
            public final InventoryClickEvent event;
            GUIButton button;

            public Click(GUIButton button, InventoryClickEvent event) {
                this.button = button;
                this.event = event;
            }
        }
    }
}
