package co.kepler.fastcraftplus.gui;

import org.bukkit.inventory.ItemStack;

public class GuiButton {
	private ItemStack item;
	
	/**
	 * Create a new InvButton.
	 * @param item The item that represents the button.
	 */
	public GuiButton(ItemStack item) {
		this.item = item;
	}
	
	/**
	 * Get the item that represents this button.
	 * @return Returns the item that represents this button.
	 */
	public ItemStack getItem() {
		return item;
	}
}
