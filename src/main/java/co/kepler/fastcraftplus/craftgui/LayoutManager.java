package co.kepler.fastcraftplus.craftgui;

import co.kepler.fastcraftplus.FastCraft;
import co.kepler.fastcraftplus.config.PluginConfig;
import co.kepler.fastcraftplus.craftgui.buttons.*;
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
        PluginConfig config = FastCraft.config();
        int toolGap = config.getToolbar_gap() ? 1 : 0;
        LayoutFastCraft layout = new LayoutFastCraft(gui, new LayoutRecipesItems(gui), toolGap);

        layout.setToolbarButton(config.getToolbar_layout_pagePrev(), new GUIButtonPagePrev(layout));
        layout.setToolbarButton(config.getToolbar_layout_pageNext(), new GUIButtonPageNext(layout));
        layout.setToolbarButton(config.getToolbar_layout_multiplier(), new GUIButtonMultiplier(gui));
        layout.setToolbarButton(config.getToolbar_layout_workbench(), new GUIButtonWorkbench(gui));
        layout.setToolbarButton(config.getToolbar_layout_refresh(), new GUIButtonRefresh(layout));

        return layout;
    }
}
