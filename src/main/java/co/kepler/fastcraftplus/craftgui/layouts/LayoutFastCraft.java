package co.kepler.fastcraftplus.craftgui.layouts;

import co.kepler.fastcraftplus.api.gui.Layout;
import co.kepler.fastcraftplus.api.gui.LayoutMulti;
import co.kepler.fastcraftplus.craftgui.GUIFastCraft;

/**
 * The button layout for the FastCraft GUI.
 */
public class LayoutFastCraft extends LayoutMulti {
    private static final int NAV_BUFFER = 1;

    private final GUIFastCraft gui;
    private final LayoutRecipes recipesLayout;

    public LayoutFastCraft(GUIFastCraft gui) {
        super(new LayoutRecipesItems(gui), new Layout(), gui.getHeight() - 1 - NAV_BUFFER);
        this.gui = gui;
    }

    @Override
    public void setHeight(int height) {
        super.setHeight(height);
        setTopLayoutHeight(height - 2);
    }
}
