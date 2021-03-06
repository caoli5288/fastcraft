package co.kepler.fastcraft.craftgui;

import co.kepler.fastcraft.FastCraft;
import co.kepler.fastcraft.config.PluginConfig;
import co.kepler.fastcraft.craftgui.buttons.*;
import co.kepler.fastcraft.craftgui.layouts.LayoutFastCraft;
import co.kepler.fastcraft.craftgui.layouts.LayoutRecipesItems;

/**
 * Provides a layout for the FastCraft interface.
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
        PluginConfig config = FastCraft.config();
        int toolGap = config.toolbar_gap() ? 1 : 0;
        LayoutFastCraft layout = new LayoutFastCraft(gui, new LayoutRecipesItems(gui), toolGap);

        layout.setToolbarButton(config.toolbar_layout_pagePrev(), new GUIButtonPagePrev(layout));
        layout.setToolbarButton(config.toolbar_layout_pageNext(), new GUIButtonPageNext(layout));
        layout.setToolbarButton(config.toolbar_layout_multiplier(), new GUIButtonMultiplier(gui));
        layout.setToolbarButton(config.toolbar_layout_workbench(), new GUIButtonWorkbench(gui));
        layout.setToolbarButton(config.toolbar_layout_refresh(), new GUIButtonRefresh(layout));

        return layout;
    }
}
