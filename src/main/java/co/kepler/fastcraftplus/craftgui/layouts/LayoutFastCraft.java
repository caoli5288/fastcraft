package co.kepler.fastcraftplus.craftgui.layouts;

import co.kepler.fastcraftplus.api.gui.GUIButton;
import co.kepler.fastcraftplus.api.gui.Layout;
import co.kepler.fastcraftplus.api.gui.LayoutMulti;
import co.kepler.fastcraftplus.craftgui.GUIFastCraft;

/**
 * The button layout for the FastCraft GUI.
 */
public class LayoutFastCraft extends LayoutMulti {
    private static final int NAV_BUFFER = 1;

    private final GUIFastCraft gui;

    public LayoutFastCraft(GUIFastCraft gui, LayoutRecipes recipesLayout) {
        super(recipesLayout, new Layout(), gui.getHeight() - 1 - NAV_BUFFER);
        this.gui = gui;
    }

    @Override
    public void setHeight(int height) {
        super.setHeight(height);
        setTopLayoutHeight(height - 2);
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
        getBottomLayout().setButton(NAV_BUFFER, col, button);
    }

    /**
     * Get a button from the GUI's toolbar.
     *
     * @param col The column to get the button from.
     * @return Returns the toolbar button in the specified column.
     */
    public GUIButton getToolbarButton(int col) {
        return getBottomLayout().getButton(NAV_BUFFER, col);
    }
}
