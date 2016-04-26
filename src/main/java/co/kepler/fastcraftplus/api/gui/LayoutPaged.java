package co.kepler.fastcraftplus.api.gui;

/**
 * A GUI layout that is organized into pages.
 */
public class LayoutPaged extends Layout {
    private int page = 0;
    private int maxSlotIndex = 0;

    @Override
    public void setButton(int slot, GUIButton button) {
        super.setButton(slot, button);
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
     *
     * @return Returns the number of buttons shown on each page of the GUI.
     */
    public int getButtonsPerPage() {
        return 9 * getHeight();
    }

    /**
     * Get the number of pages.
     *
     * @return Returns the number of pages.
     */
    public int getPageCount() {
        return maxSlotIndex / getButtonsPerPage() + 1;
    }

    /**
     * Get the current page.
     *
     * @return Returns the current page.
     */
    public int getPage() {
        return page;
    }

    /**
     * Set the current page.
     *
     * @param page Which page to go to.
     */
    public void setPage(int page) {
        if (page < 0) page = 0;
        if (page > getPageCount() - 1) page = getPageCount() - 1;
        this.page = page;
    }

    /**
     * Go to the previous page.
     */
    public void prevPage() {
        setPage(page - 1);
    }

    /**
     * Go to the next page.
     */
    public void nextPage() {
        setPage(page + 1);
    }

    /**
     * See if the current page is the first page.
     *
     * @return Returns true if the current page is the first page.
     */
    public boolean isPageFirst() {
        return page == 0;
    }

    /**
     * See if the current page is the last page.
     *
     * @return Returns true if the current page is the last page.
     */
    public boolean isPageLast() {
        return page == getPageCount() - 1;
    }

    @Override
    public GUIButton getButton(int slot) {
        int[] rawSlotPos = getSlotPos(slot);
        slot += getButtonsPerPage() * page;
        return super.getButton(slot);
    }
}
