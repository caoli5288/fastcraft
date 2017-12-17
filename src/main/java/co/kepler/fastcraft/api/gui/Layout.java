package co.kepler.fastcraft.api.gui;

import java.util.HashMap;
import java.util.Map;

/**
 * Holds buttons and their locations.
 */
public class Layout {
    protected Map<Integer, GUIButton> buttons;
    private int height;

    /**
     * Create a new InvButtonLayout.
     */
    public Layout() {
        buttons = new HashMap<>();
    }

    /**
     * Get the inventory slot index given a row and a column.
     *
     * @param row The row to find the index of.
     * @param col The column to find the index of.
     * @return Returns the inventory slot index of the row and column.
     */
    public static int getSlot(int row, int col) {
        assert row >= 0 : "Row (" + row + ") must not be negative";
        assert 0 <= col && col < 9 : "Column (" + col + ") must be from 0 to 8";
        return row * 9 + col;
    }

    /**
     * Get the row and column of an inventory slot index.
     *
     * @param slot The slot to find the row and column of.
     * @return Returns the row and column of a given slot index.
     */
    public static int[] getSlotPos(int slot) {
        return new int[]{slot / 9, slot % 9};
    }

    /**
     * Get the button in the specified inventory slot.
     *
     * @param slot The inventory slot to get the button from.
     * @return Returns the button in the specified inventory slot.
     */
    public GUIButton getButton(int slot) {
        assert slot >= 0 : "Slot (" + slot + ") must not be negative";
        return buttons.get(slot);
    }

    /**
     * Get the button in the specified row and column.
     *
     * @param row The row to get the button from.
     * @param col The column to get the button from.
     * @return Returns the button in the specified row and column.
     */
    public GUIButton getButton(int row, int col) {
        return getButton(getSlot(row, col));
    }

    /**
     * Adds a button to the layout at a given row and column.
     *
     * @param button The button to add.
     * @param slot   The slot index of the button.
     */
    public void setButton(int slot, GUIButton button) {
        assert slot >= 0 : "Slot (" + slot + ") must not be negative";
        if (button == null) {
            buttons.remove(slot);
        } else {
            buttons.put(slot, button);
        }
    }

    /**
     * Adds a button to the layout at a given row and column.
     *
     * @param button The button to add.
     * @param row    The row of the button.
     * @param col    The column of the button.
     */
    public void setButton(int row, int col, GUIButton button) {
        setButton(getSlot(row, col), button);
    }

    /**
     * Removes a button from the layout at a given row and column.
     *
     * @param slot The slot index of the button to remove.
     */
    public void removeButton(int slot) {
        setButton(slot, null);
    }

    /**
     * Removes a button from the layout at a given row and column.
     *
     * @param row The row to remove the button from.
     * @param col The column to remove the button from.
     */
    public void removeButton(int row, int col) {
        setButton(row, col, null);
    }

    /**
     * Remove all buttons from the layout.
     */
    public void clearButtons() {
        buttons.clear();
    }

    /**
     * Get the number of rows in the layout.
     *
     * @return Returns the height of the layout.
     */
    public int getHeight() {
        return height;
    }

    /**
     * Set the number of rows in the layout.
     *
     * @param height The height of the layout.
     */
    public void setHeight(int height) {
        this.height = height;
    }
}
