package co.kepler.fastcraftplus.craftgui;

import co.kepler.fastcraftplus.craftgui.buttons.GUIButtonPageNext;
import co.kepler.fastcraftplus.craftgui.buttons.GUIButtonPagePrev;
import co.kepler.fastcraftplus.craftgui.buttons.GUIButtonRefresh;
import co.kepler.fastcraftplus.craftgui.buttons.GUIButtonWorkbench;
import co.kepler.fastcraftplus.craftgui.layouts.LayoutFastCraft;
import co.kepler.fastcraftplus.craftgui.layouts.LayoutRecipesItems;

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
        LayoutFastCraft layout = new LayoutFastCraft(gui, new LayoutRecipesItems(gui));

        layout.setToolbarButton(0, new GUIButtonPagePrev(layout));
        layout.setToolbarButton(4, new GUIButtonWorkbench(gui));
        layout.setToolbarButton(5, new GUIButtonRefresh(layout));
        layout.setToolbarButton(8, new GUIButtonPageNext(layout));

        return layout;
    }
}
