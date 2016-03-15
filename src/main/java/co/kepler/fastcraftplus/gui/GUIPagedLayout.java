package co.kepler.fastcraftplus.gui;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 * A GUI layout that is organized into pages.
 */
public class GUIPagedLayout extends GUILayout {
    private int page = 0;
    private int maxSlotIndex = 0;

    @Override
    public void setPendingButton(int slot, GUIButton button) {
        super.setPendingButton(slot, button);
        // Keep track of the index of the last button, so the last page can be known.
        if (button == null) {
            maxSlotIndex = 0;
            for (Integer i : buttons.keySet()) {
                maxSlotIndex = Math.max(maxSlotIndex, i);
            }
        } else {
            maxSlotIndex = Math.max(maxSlotIndex, slot);
        }
    }

    /**
     * Get the number of buttons shown on each page of the GUI.
     * @return Returns the number of buttons shown on each page of the GUI.
     */
    public int getButtonsPerPage() {
        return 9 * getHeight();
    }

    /**
     * Get the number of pages.
     * @return Returns the number of pages.
     */
    public int getPageCount() {
        return maxSlotIndex / getButtonsPerPage() + 1;
    }

    /**
     * Get the current page.
     * @return Returns the current page.
     */
    public int getPage() {
        return page;
    }

    /**
     * Set the current page.
     * @param page Which page to go to.
     */
    public void setPage(int page) {
        if (page < 0) page = 0;
        if (page > getPageCount()) page = getPageCount() - 1;
        this.page = page;
    }

    @Override
    public GUIButton getButton(int slot) {
        int[] rawSlotPos = getSlotPos(slot);
        slot += getButtonsPerPage() * page;
        return super.getButton(slot);
    }
}
