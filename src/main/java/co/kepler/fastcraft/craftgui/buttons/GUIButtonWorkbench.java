package co.kepler.fastcraft.craftgui.buttons;

import co.kepler.fastcraft.FastCraft;
import co.kepler.fastcraft.api.gui.GUI;
import co.kepler.fastcraft.api.gui.GUIButton;
import co.kepler.fastcraft.api.gui.GUIItemBuilder;
import co.kepler.fastcraft.craftgui.GUIFastCraft;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * A button that refreshes the recipes in the FastCraft interface.
 */
public class GUIButtonWorkbench extends GUIButton {
    private final GUIFastCraft gui;
    private final ItemStack item;

    public GUIButtonWorkbench(GUIFastCraft gui) {
        this.gui = gui;
        item = new GUIItemBuilder(Material.WORKBENCH)
                .setDisplayName(FastCraft.lang().gui_toolbar_workbench_title())
                .setLore(FastCraft.lang().gui_toolbar_workbench_description())
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
        final HumanEntity clicker = invEvent.getWhoClicked();
        final Location location = this.gui.getLocation();
        new BukkitRunnable() {
            public void run() {
                clicker.openWorkbench(location, location == null);
            }
        }.runTask(FastCraft.getInstance());
        return true;
    }
}
