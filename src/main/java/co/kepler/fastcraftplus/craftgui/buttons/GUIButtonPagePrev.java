package co.kepler.fastcraftplus.craftgui.buttons;

import co.kepler.fastcraftplus.FastCraft;
import co.kepler.fastcraftplus.api.gui.GUI;
import co.kepler.fastcraftplus.api.gui.GUIButtonAbstract;
import co.kepler.fastcraftplus.api.gui.GUIItemBuilder;
import co.kepler.fastcraftplus.api.gui.LayoutPaged;
import co.kepler.fastcraftplus.craftgui.layouts.LayoutFastCraft;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 * A button that goes to the previous page in a paged layout.
 */
public class GUIButtonPagePrev extends GUIButtonAbstract {
    private final LayoutFastCraft layout;

    public GUIButtonPagePrev(LayoutFastCraft layout) {
        this.layout = layout;
    }

    @Override
    public ItemStack getItem() {
        LayoutPaged layout = this.layout.getTopLayout();
        int prev = layout.getPage() - 1, count = layout.getPageCount(), page = layout.getPage();

        return new GUIItemBuilder(Material.ARROW)
                .setDisplayName(FastCraft.lang().gui_toolbar_pagePrev_title())
                .setLore(FastCraft.lang().gui_toolbar_pagePrev_description(prev, count, page))
                .setHideInfo(true)
                .build();
    }

    @Override
    public boolean isVisible() {
        return !layout.getTopLayout().isPageFirst();
    }

    @Override
    public boolean onClick(GUI gui, InventoryClickEvent invEvent) {
        layout.getTopLayout().prevPage();
        gui.updateLayout();
        return true;
    }
}
