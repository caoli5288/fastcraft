package co.kepler.fastcraftplus.gui;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.inventory.Inventory;

/**
 * Holds buttons and their locations.
 */
public class GuiLayout {
	private final Map<Integer, GuiButton> buttons;
	
	/**
	 * Create a new InvButtonLayout.
	 */
	public GuiLayout() {
		buttons = new HashMap<Integer, GuiButton>();
	}
	
	/**
	 * Get the button in the specified inventory slot.
	 * @param slot The inventory slot to get the button from.
	 * @return Returns the button in the specified inventory slot.
	 */
	public GuiButton getButton(int slot) {
		return buttons.get(slot);
	}
	
	/**
	 * Get the button in the specified row and column.
	 * @param row The row to get the button from.
	 * @param col The column to get the button from.
	 * @return Returns the button in the specified row and column.
	 */
	public GuiButton getButton(int row, int col) {
		return getButton(getSlot(row, col));
	}
	
	/**
	 * Adds a button to the layout at a given row and column.
	 * @param button The button to add.
	 * @param row The row of the button.
	 * @param col The column of the button.
	 */
	public void setButton(GuiButton button, int row, int col) {
		buttons.put(getSlot(row, col), button);
	}
	
	/**
	 * Removes a button from the layout at a given row and column.
	 * @param row The row to remove the button from.
	 * @param col The column to remove the button from.
	 */
	public void removeButton(int row, int col) {
		buttons.remove(getSlot(row, col));
	}
	
	/**
	 * Apply the button layout to an inventory.
	 * @param inv The inventory to apply the layout to.
	 */
	public void apply(Inventory inv) {
		inv.clear();
		int invSize = inv.getSize();
		for (int i : buttons.keySet()) {
			if (i >= invSize) continue;
			inv.setItem(i, buttons.get(i).getItem());
		}
	}
	
	/**
	 * Get the inventory slot index given a row and a column.
	 * @param row The row to find the index of.
	 * @param col The column to find the index of.
	 * @return Returns the inventory slot index of the row and column.
	 */
	public static int getSlot(int row, int col) {
		assert row < 9;
		return row * 9 + col;
	}
	
	/**
	 * Get the row and column of an inventory slot index.
	 * @param slot The slot to find the row and column of.
	 * @return Returns the row and column of a given slot index.
	 */
	public static int[] getRowCol(int slot) {
		return new int[]{slot / 9, slot % 9};
	}
}
