package co.kepler.fastcraft.api.gui;

/**
 * A GUI layout that contains two other GUI layouts, one shown above the other.
 */
public class LayoutMulti extends Layout {
    private int topHeight;
    private Layout topLayout;
    private Layout bottomLayout;

    /**
     * Create a new LayoutMulti.
     *
     * @param topLayout       The top GUI layout.
     * @param bottomLayout    The bottom GUI layout.
     * @param topLayoutHeight The height of the top layout.
     */
    public LayoutMulti(Layout topLayout, Layout bottomLayout, int topLayoutHeight) {
        this.topLayout = topLayout;
        this.bottomLayout = bottomLayout;

        setTopLayoutHeight(topLayoutHeight);
    }

    @Override
    public void setHeight(int height) {
        super.setHeight(height);
        updateLayoutHeights();
    }

    /**
     * Get the top GUI layout.
     *
     * @return Returns the top GUI layout.
     */
    public Layout getTopLayout() {
        return topLayout;
    }

    /**
     * Set the top GUI layout.
     *
     * @param layout The layout to set as the top GUI layout.
     */
    public void setTopLayout(Layout layout) {
        topLayout = layout;
        updateLayoutHeights();
    }

    /**
     * Get the height of the top layout.
     *
     * @return Returns the height of the top layout.
     */
    public int getTopLayoutHeight() {
        return topHeight;
    }

    /**
     * Set the height of the top layout.
     *
     * @param topHeight The new height of the top layout.
     */
    public void setTopLayoutHeight(int topHeight) {
        assert topHeight >= 0 : "Height (" + topHeight + ") must not be negative";
        this.topHeight = topHeight;
        updateLayoutHeights();
    }

    /**
     * Get the bottom GUI layout.
     *
     * @return Returns the bottom GUI layout.
     */
    public Layout getBottomLayout() {
        return bottomLayout;
    }

    /**
     * Set the bottom GUI layout.
     *
     * @param layout The layout to set as the bottom GUI layout.
     */
    public void setBottomLayout(Layout layout) {
        bottomLayout = layout;
        updateLayoutHeights();
    }

    /**
     * Get the height of the bottom layout.
     *
     * @return Returns the height of the bottom layout.
     */
    public int getBottomLayoutHeight() {
        return getHeight() - topHeight;
    }

    /**
     * Set the heights of the top and bottom layouts.
     */
    private void updateLayoutHeights() {
        if (topLayout != null) topLayout.setHeight(topHeight);
        if (bottomLayout != null) bottomLayout.setHeight(getBottomLayoutHeight());
    }

    /**
     * Get the layout this slot is pointing to, and the slot within the layout.
     *
     * @param slot The slot to get the layout.
     * @return Returns the layout this slot is a part of, and the slot within the layout.
     */
    public LayoutSlot getLayoutSlot(int slot) {
        int[] rowCol = getSlotPos(slot);
        if (rowCol[0] < topHeight) {
            // If top layout, get button from top layout.
            return new LayoutSlot(topLayout, slot);
        } else {
            // If bottom layout, get button from bottom layout.
            return new LayoutSlot(bottomLayout, getSlot(rowCol[0] - topHeight, rowCol[1]));
        }
    }

    @Override
    public GUIButton getButton(int slot) {
        LayoutSlot ls = getLayoutSlot(slot);
        return ls.layout.getButton(ls.slot);
    }

    @Override
    public void setButton(int slot, GUIButton button) {
        LayoutSlot ls = getLayoutSlot(slot);
        ls.layout.setButton(ls.slot, button);
    }

    /**
     * Contains a layout, and a slot within the layout.
     */
    public static class LayoutSlot {
        public final Layout layout;
        public final int slot;

        public LayoutSlot(Layout layout, int slot) {
            this.layout = layout;
            this.slot = slot;
        }
    }
}
