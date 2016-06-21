package co.kepler.fastcraftplus.craftgui.buttons;

import co.kepler.fastcraftplus.FastCraftPlus;
import co.kepler.fastcraftplus.api.gui.GUI;
import co.kepler.fastcraftplus.api.gui.GUIButton;
import co.kepler.fastcraftplus.api.gui.GUIItemBuilder;
import co.kepler.fastcraftplus.api.gui.LayoutPaged;
import co.kepler.fastcraftplus.craftgui.layouts.LayoutFastCraft;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 * A button that goes to the previous page in a paged layout.
 */
public class GUIButtonPagePrev extends GUIButton {
    private final LayoutFastCraft layout;

    public GUIButtonPagePrev(LayoutFastCraft layout) {
        this.layout = layout;
    }

    @Override
    public ItemStack getItem() {
        LayoutPaged layout = this.layout.getTopLayout();
        int prev = layout.getPage(), count = layout.getPageCount(), page = layout.getPage() + 1;

        return new GUIItemBuilder(Material.ARROW)
                .setDisplayName(FastCraftPlus.lang().gui_toolbar_pagePrev_title())
                .setLore(FastCraftPlus.lang().gui_toolbar_pagePrev_description(prev, count, page))
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
