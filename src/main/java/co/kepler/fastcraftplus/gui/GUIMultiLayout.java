package co.kepler.fastcraftplus.gui;

import javax.naming.OperationNotSupportedException;

/**
 * A GUI layout that contains two other GUI layouts, one shown above the other.
 */
public class GUIMultiLayout extends GUILayout {
    private static final String BUTTON_METHOD_ERR =
            "Pending buttons should be accessed/modified with the MultiLayout's sub-layouts";

    private int topHeight;
    private GUILayout topLayout;
    private GUILayout bottomLayout;

    /**
     * Create a new GUIMultiLayout.
     * @param topLayout The top GUI layout.
     * @param bottomLayout The bottom GUI layout.
     * @param topLayoutHeight The height of the top layout.
     */
    public GUIMultiLayout(GUILayout topLayout, GUILayout bottomLayout, int topLayoutHeight) {
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
     * @return Returns the top GUI layout.
     */
    public GUILayout getTopLayout() {
        return topLayout;
    }

    /**
     * Set the top GUI layout.
     * @param layout The layout to set as the top GUI layout.
     */
    public void setTopLayout(GUILayout layout) {
        topLayout = layout;
        updateLayoutHeights();
    }

    /**
     * Get the height of the top layout.
     * @return Returns the height of the top layout.
     */
    public int getTopLayoutHeight() {
        return topHeight;
    }

    /**
     * Get the bottom GUI layout.
     * @return Returns the bottom GUI layout.
     */
    public GUILayout getBottomLayout() {
        return bottomLayout;
    }

    /**
     * Set the bottom GUI layout.
     * @param layout The layout to set as the bottom GUI layout.
     */
    public void setBottomLayout(GUILayout layout) {
        bottomLayout = layout;
        updateLayoutHeights();
    }

    /**
     * Get the height of the bottom layout.
     * @return Returns the height of the bottom layout.
     */
    public int getBottomLayoutHeight() {
        return getHeight() - topHeight;
    }

    /**
     * Set the height of the top layout.
     * @param topHeight The new height of the top layout.
     */
    public void setTopLayoutHeight(int topHeight) {
        assert topHeight >= 0 : "Height (" + topHeight + ") must not be negative";
        this.topHeight = topHeight;
        updateLayoutHeights();
    }

    /**
     * Set the heights of the top and bottom layouts.
     */
    private void updateLayoutHeights() {
        topLayout.setHeight(topHeight);
        bottomLayout.setHeight(getBottomLayoutHeight());
    }

    @Override
    public GUIButton getButton(int slot) {
        int[] rowCol = getSlotPos(slot);
        if (rowCol[0] < topHeight) {
            // If top layout, get button from top layout.
            return topLayout.getButton(slot);
        } else {
            // If bottom layout, get button from bottom layout.
            return bottomLayout.getButton(rowCol[0] - topHeight, rowCol[1]);
        }
    }

    @Override
    public GUIButton getPendingButton(int slot) throws UnsupportedOperationException {
        throw new UnsupportedOperationException(BUTTON_METHOD_ERR);
    }

    @Override
    public void setPendingButton(int slot, GUIButton button) {
        throw new UnsupportedOperationException(BUTTON_METHOD_ERR);
    }
}
