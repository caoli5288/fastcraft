package co.kepler.fastcraftplus.gui;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public abstract class GUIButton {
	private ItemStack item;
	
	/**
	 * Create a new InvButton.
	 * @param item The item that represents the button.
	 */
	public GUIButton(ItemStack item) {
		this.item = item;
	}
	
	/**
	 * Get the item that represents this button.
	 * @return Returns the item that represents this button.
	 */
	public ItemStack getItem() {
		return item;
	}
	
	/**
	 * Called when the button is clicked.
	 * @param gui The GUI in which the button was clicked.
	 * @param invEvent The inventory event triggered by the click.
	 */
	public abstract void onClick(GUI gui, InventoryClickEvent invEvent);
}
