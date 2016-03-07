package co.kepler.fastcraftplus.gui;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.inventory.Inventory;

/**
 * Holds buttons and their locations.
 */
public class GUILayout {
	protected final Map<Integer, GUIButton> buttons;
	
	/**
	 * Create a new InvButtonLayout.
	 */
	public GUILayout() {
		buttons = new HashMap<Integer, GUIButton>();
	}
	
	/**
	 * Get the button in the specified inventory slot.
	 * @param slot The inventory slot to get the button from.
	 * @return Returns the button in the specified inventory slot.
	 */
	public GUIButton getButton(int slot, GUI gui) {
		assert slot >= 0;
		return buttons.get(slot);
	}
	
	/**
	 * Get the button in the specified row and column.
	 * @param row The row to get the button from.
	 * @param col The column to get the button from.
	 * @return Returns the button in the specified row and column.
	 */
	public GUIButton getButton(int row, int col, GUI gui) {
		return getButton(getSlot(row, col), gui);
	}
	
	/**
	 * Adds a button to the layout at a given row and column.
	 * @param button The button to add.
	 * @param slot The slot index of the button.
	 */
	public void setButton(int slot, GUIButton button) {
		assert slot >= 0;
		if (button == null) {
			buttons.remove(slot);
		} else {
			buttons.put(slot, button);
		}
	}
	
	/**
	 * Adds a button to the layout at a given row and column.
	 * @param button The button to add.
	 * @param row The row of the button.
	 * @param col The column of the button.
	 */
	public void setButton(int row, int col, GUIButton button) {
		setButton(getSlot(row, col), button);
	}
	
	/**
	 * Removes a button from the layout at a given row and column.
	 * @param row The row to remove the button from.
	 * @param col The column to remove the button from.
	 */
	public void removeButton(int row, int col) {
		setButton(row, col, null);
	}
	
	/**
	 * Apply the button layout to an inventory.
	 * @param inv The inventory to apply the layout to.
	 */
	public void apply(Inventory inv) {
		inv.clear();
		int invSize = inv.getSize();
		for (int i : buttons.keySet()) {
			GUIButton button = buttons.get(i);
			if (i >= invSize || !button.isVisible()) continue;
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
		assert 0 <= row && row < 9 && 0 <= col;
		return row * 9 + col;
	}
	
	/**
	 * Get the row and column of an inventory slot index.
	 * @param slot The slot to find the row and column of.
	 * @return Returns the row and column of a given slot index.
	 */
	public static int[] getSlotPos(int slot) {
		return new int[]{slot / 9, slot % 9};
	}
}
