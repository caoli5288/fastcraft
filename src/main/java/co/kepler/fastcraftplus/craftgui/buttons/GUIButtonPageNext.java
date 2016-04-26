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
public class GUIButtonPageNext extends GUIButtonAbstract {
    private final LayoutPaged layout;

    public GUIButtonPageNext(LayoutPaged layout) {
        this.layout = layout;
    }

    @Override
    public ItemStack getItem() {
        int next = layout.getPage() + 1, count = layout.getPageCount(), page = layout.getPage();

        return new GUIItemBuilder(Material.ARROW)
                .setDisplayName(FastCraft.lang().gui_toolbar_pageNext_title())
                .setLore(FastCraft.lang().gui_toolbar_pageNext_description(next, count, page))
                .setHideInfo(true)
                .build();
    }

    @Override
    public boolean isVisible() {
        return !layout.isPageLast();
    }

    @Override
    public boolean onClick(GUI gui, InventoryClickEvent invEvent) {
        layout.nextPage();
        gui.updateLayout();
        return true;
    }
}
