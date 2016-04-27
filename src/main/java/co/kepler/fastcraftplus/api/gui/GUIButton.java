package co.kepler.fastcraftplus.api.gui;

import org.bukkit.Sound;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 * A button for the GUI that players can interact with.
 */
public abstract class GUIButton {
    public static final Sound DEFAULT_CLICK_SOUND;

    static {
        Sound clickSound;
        try {
            clickSound = Sound.valueOf("UI_BUTTON_CLICK"); // 1.9
        } catch (IllegalArgumentException e) {
            clickSound = Sound.valueOf("CLICK"); // 1.8 and earlier
        }
        DEFAULT_CLICK_SOUND = clickSound;
    }

    /**
     * Get the item that represents this button.
     *
     * @return Returns the item that represents this button.
     */
    public abstract ItemStack getItem();

    /**
     * See if this button is visible in the GUI.
     *
     * @return Returns true if the button should be visible in the GUI.
     */
    public abstract boolean isVisible();

    /**
     * Get the sound to be played when the button is clicked.
     */
    public Sound getClickSound() {
        return DEFAULT_CLICK_SOUND;
    }

    /**
     * Called when the button is clicked.
     *
     * @param gui      The clicked GUI.
     * @param invEvent The inventory event triggered by the click.
     * @return Returns true if the button was clicked successfully.
     */
    public abstract boolean onClick(GUI gui, InventoryClickEvent invEvent);

    public GUIButton copy() {
        return new GUIButtonCopy(this);
    }

    /**
     * A copy of a button.
     */
    private class GUIButtonCopy extends GUIButton {
        private final GUIButton button;
        private final ItemStack item;
        private final boolean visible;

        public GUIButtonCopy(GUIButton copy) {
            this.button = copy;
            item = copy.getItem();
            visible = copy.isVisible();
        }

        @Override
        public ItemStack getItem() {
            return item.clone();
        }

        @Override
        public boolean isVisible() {
            return visible;
        }

        @Override
        public boolean onClick(GUI gui, InventoryClickEvent invEvent) {
            return button.onClick(gui, invEvent);
        }
    }
}
