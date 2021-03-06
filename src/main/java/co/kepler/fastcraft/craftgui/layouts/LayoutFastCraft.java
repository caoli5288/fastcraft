package co.kepler.fastcraft.craftgui.layouts;

import co.kepler.fastcraft.api.gui.GUIButton;
import co.kepler.fastcraft.api.gui.Layout;
import co.kepler.fastcraft.api.gui.LayoutMulti;
import co.kepler.fastcraft.craftgui.GUIFastCraft;

/**
 * The button layout for the FastCraft GUI.
 */
public class LayoutFastCraft extends LayoutMulti {
    private final int toolGap;
    private final GUIFastCraft gui;

    public LayoutFastCraft(GUIFastCraft gui, LayoutRecipes recipesLayout, int toolGap) {
        super(recipesLayout, new Layout(), gui.getHeight() - 1 - toolGap);
        this.toolGap = toolGap;
        this.gui = gui;
    }

    @Override
    public void setHeight(int height) {
        super.setHeight(height);
        setTopLayoutHeight(height - 1 - toolGap);
    }

    @Override
    public void setTopLayout(Layout layout) {
        assert layout instanceof LayoutRecipes : "Layout must be a recipes layout";
        super.setTopLayout(layout);
    }

    @Override
    public LayoutRecipes getTopLayout() {
        return (LayoutRecipes) super.getTopLayout();
    }

    /**
     * Set a button in the GUI's toolbar.
     *
     * @param col    The column to put the button in.
     * @param button The button to add to the toolbar.
     */
    public void setToolbarButton(int col, GUIButton button) {
        if (col < 0 || col > 8) return;
        getBottomLayout().setButton(toolGap, col, button);
    }

    /**
     * Get a button from the GUI's toolbar.
     *
     * @param col The column to get the button from.
     * @return Returns the toolbar button in the specified column.
     */
    public GUIButton getToolbarButton(int col) {
        return getBottomLayout().getButton(toolGap, col);
    }
}
