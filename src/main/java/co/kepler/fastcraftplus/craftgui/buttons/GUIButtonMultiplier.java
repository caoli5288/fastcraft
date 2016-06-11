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
    private final GUIFastCraft gui;
    private final int[] multOrder;
    private final int min, max;

    private int multIndex = 0;

    public GUIButtonMultiplier(GUIFastCraft gui) {
        this.gui = gui;

        multOrder = FastCraft.config().getToolbar_multiplierOrder();
        min = multOrder[0];
        max = multOrder[multOrder.length - 1];
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
        }
        if (inventoryClickEvent.isShiftClick()) {
            // If shift click, increment by 1
            mult += inventoryClickEvent.isLeftClick() ? 1 : -1;
        } else {
            // If normal click, increment to the next power of 2
            int newMult;
            if (inventoryClickEvent.isLeftClick()) {
                // Increase
                newMult = min;
                for (int m : multOrder) {
                    if (m > mult) {
                        newMult = m;
                        break;
                    }
                }
            } else {
                // Decrease
                newMult = max;
                for (int i = multOrder.length - 1; i >= 0; i--) {
                    int m = multOrder[i];
                    if (m < mult) {
                        newMult = m;
                        break;
                    }
                }
            }
            mult = newMult;
        }

        // Set new multiplier
        if (mult < min) {
            mult = max;
        } else if (mult > max) {
            mult = min;
        }
        this.gui.setMultiplier(mult);
        this.gui.updateLayout();

        return true;
    }
}
