package co.kepler.fastcraftplus.craftgui;

import co.kepler.fastcraftplus.craftgui.layouts.LayoutFastCraft;

/**
 * Provides a layout for the FastCraft+ interface.
 */
public class LayoutManager {
    private static LayoutManager layoutManager = new LayoutManager();

    public static LayoutManager getLayoutManager() {
        return layoutManager;
    }

    public static void setLayoutManager(LayoutManager layoutManager) {
        LayoutManager.layoutManager = layoutManager;
    }

    public LayoutFastCraft getNewLayout(GUIFastCraft gui) {
        LayoutFastCraft layout = new LayoutFastCraft(gui);

        LayoutFastCraft layout
    }
}
