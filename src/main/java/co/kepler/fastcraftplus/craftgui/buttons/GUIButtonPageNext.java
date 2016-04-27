package co.kepler.fastcraftplus.craftgui.buttons;

import co.kepler.fastcraftplus.FastCraft;
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
public class GUIButtonPageNext extends GUIButton {
    private final LayoutFastCraft layout;

    public GUIButtonPageNext(LayoutFastCraft layout) {
        this.layout = layout;
    }

    @Override
    public ItemStack getItem() {
        LayoutPaged layout = this.layout.getTopLayout();
        int next = layout.getPage() + 2, count = layout.getPageCount(), page = layout.getPage() + 1;

        return new GUIItemBuilder(Material.ARROW)
                .setDisplayName(FastCraft.lang().gui_toolbar_pageNext_title())
                .setLore(FastCraft.lang().gui_toolbar_pageNext_description(next, count, page))
                .setHideInfo(true)
                .build();
    }

    @Override
    public boolean isVisible() {
        return !layout.getTopLayout().isPageLast();
    }

    @Override
    public boolean onClick(GUI gui, InventoryClickEvent invEvent) {
        layout.getTopLayout().nextPage();
        gui.updateLayout();
        return true;
    }
}
