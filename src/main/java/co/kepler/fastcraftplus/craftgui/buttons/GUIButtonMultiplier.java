package co.kepler.fastcraftplus.craftgui.buttons;

import co.kepler.fastcraftplus.FastCraft;
import co.kepler.fastcraftplus.api.gui.GUI;
import co.kepler.fastcraftplus.api.gui.GUIButton;
import co.kepler.fastcraftplus.craftgui.GUIFastCraft;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Controls the GUI's multiplier.
 */
public class GUIButtonMultiplier extends GUIButton {
    private static final int MAX_MULTIPLIER = 64;

    private final GUIFastCraft gui;

    public GUIButtonMultiplier(GUIFastCraft gui) {
        this.gui = gui;
    }

    @Override
    public ItemStack getItem() {
        int mult = gui.getMultiplier();
        ItemStack result = new ItemStack(Material.ANVIL, mult);

        // Set display name and lore
        ItemMeta meta = result.getItemMeta();
        meta.setDisplayName(FastCraft.lang().gui_toolbar_multiplier_title(mult));
        meta.setLore(FastCraft.lang().gui_toolbar_multiplier_description(mult));
        result.setItemMeta(meta);

        return result;
    }

    @Override
    public boolean isVisible() {
        return true;
    }

    @Override
    public boolean onClick(GUI gui, InventoryClickEvent inventoryClickEvent) {
        int mult = this.gui.getMultiplier();

        // If left click, increase. Otherwise, decrease.
        if (inventoryClickEvent.getClick().isCreativeAction()) {
            // If middle click, reset to 1
            mult = 1;
        } if (inventoryClickEvent.isShiftClick()) {
            // If shift click, increment by 1
            if (inventoryClickEvent.isLeftClick()) {
                mult++;
            } else {
                mult--;
            }
        } else {
            // If normal click, increment to the next power of 2
            double pow = Math.log(mult) / Math.log(2);
            if (inventoryClickEvent.isLeftClick()) {
                pow = Math.floor(++pow);
            } else if (inventoryClickEvent.isRightClick()) {
                pow = Math.ceil(--pow);
            }
            mult = (int) Math.floor(Math.pow(2, pow));
        }

        // Set new multiplier
        if (mult < 1) {
            mult = MAX_MULTIPLIER;
        } else if (mult > MAX_MULTIPLIER) {
            mult = 1;
        }
        this.gui.setMultiplier(mult);
        this.gui.updateLayout();

        return true;
    }
}
