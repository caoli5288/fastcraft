package co.kepler.fastcraftplus.craftgui.buttons;

import co.kepler.fastcraftplus.FastCraft;
import co.kepler.fastcraftplus.api.gui.GUI;
import co.kepler.fastcraftplus.api.gui.GUIButtonAbstract;
import co.kepler.fastcraftplus.api.gui.GUIItemBuilder;
import co.kepler.fastcraftplus.api.gui.LayoutPaged;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 * A button that goes to the previous page in a paged layout.
 */
public class GUIButtonPagePrev extends GUIButtonAbstract {
    private final LayoutPaged layout;

    public GUIButtonPagePrev(LayoutPaged layout) {
        this.layout = layout;
    }

    @Override
    public ItemStack getItem() {
        int prev = layout.getPage() - 1, count = layout.getPageCount(), page = layout.getPage();

        return new GUIItemBuilder(Material.ARROW)
                .setDisplayName(FastCraft.lang().gui_toolbar_pagePrev_title())
                .setLore(FastCraft.lang().gui_toolbar_pagePrev_description(prev, count, page))
                .setHideInfo(true)
                .build();
    }

    @Override
    public boolean isVisible() {
        return !layout.isPageFirst();
    }

    @Override
    public boolean onClick(GUI gui, InventoryClickEvent invEvent) {
        layout.prevPage();
        gui.updateLayout();
        return true;
    }
}
