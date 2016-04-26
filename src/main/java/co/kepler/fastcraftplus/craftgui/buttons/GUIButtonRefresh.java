package co.kepler.fastcraftplus.craftgui.buttons;

import co.kepler.fastcraftplus.FastCraft;
import co.kepler.fastcraftplus.api.gui.GUI;
import co.kepler.fastcraftplus.api.gui.GUIButton;
import co.kepler.fastcraftplus.api.gui.GUIItemBuilder;
import co.kepler.fastcraftplus.api.gui.Layout;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 * A button that refreshes the recipes in the FastCraft+ interface.
 */
public class GUIButtonRefresh extends GUIButton {
    private final Layout layout;
    private final ItemStack item;

    public GUIButtonRefresh(Layout layout) {
        this.layout = layout;
        item = new GUIItemBuilder(Material.NETHER_STAR)
                .setDisplayName(FastCraft.lang().gui_toolbar_refresh_title())
                .setLore(FastCraft.lang().gui_toolbar_refresh_description())
                .setHideInfo(true)
                .build();
    }

    @Override
    public ItemStack getItem() {
        return item.clone();
    }

    @Override
    public boolean isVisible() {
        return true;
    }

    @Override
    public boolean onClick(GUI gui, InventoryClickEvent invEvent) {
        layout.clearButtons();
        gui.updateLayout();
        return true;
    }
}
