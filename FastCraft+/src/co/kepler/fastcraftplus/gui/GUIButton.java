package co.kepler.fastcraftplus.gui;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 * A button for the GUI that players can interact with.
 */
public abstract class GUIButton {
	private ItemStack item;
	private boolean visible;
	
	/**
	 * Create a new GUIButton.
	 * @param item The item that represents the button.
	 */
	public GUIButton(ItemStack item) {
		this.item = item;
	}
	
	/**
	 * Create a new GUIButton
	 * @param item The item that represents the button.
	 * @param visible Whether the button is visible.
	 */
	public GUIButton(ItemStack item, boolean visible) {
		this(item);
		this.visible = visible;
	}
	
	/**
	 * Get the item that represents this button.
	 * @return Returns the item that represents this button.
	 */
	public ItemStack getItem() {
		return item;
	}
	
	/**
	 * See if this button is visible in the GUI.
	 * @return Returns true if the button is visible in the GUI.
	 */
	public boolean isVisible() {
		return visible;
	}
	
	/**
	 * Set the visibility of the button in the GUI.
	 * @param visible Whether the button is visible or not in the GUI.
	 */
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	
	/**
	 * Called when the button is clicked.
	 * @param gui The GUI in which the button was clicked.
	 * @param invEvent The inventory event triggered by the click.
	 */
	public abstract void onClick(GUI gui, InventoryClickEvent invEvent);
}
