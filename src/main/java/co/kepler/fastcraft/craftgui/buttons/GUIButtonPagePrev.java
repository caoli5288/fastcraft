package co.kepler.fastcraft.craftgui.buttons;

import co.kepler.fastcraft.FastCraft;
import co.kepler.fastcraft.api.gui.GUI;
import co.kepler.fastcraft.api.gui.GUIButton;
import co.kepler.fastcraft.api.gui.GUIItemBuilder;
import co.kepler.fastcraft.api.gui.LayoutPaged;
import co.kepler.fastcraft.craftgui.layouts.LayoutFastCraft;
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
