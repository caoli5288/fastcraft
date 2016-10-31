package co.kepler.fastcraftplus.craftgui.buttons;

import co.kepler.fastcraftplus.FastCraftPlus;
import co.kepler.fastcraftplus.api.gui.GUI;
import co.kepler.fastcraftplus.api.gui.GUIButton;
import co.kepler.fastcraftplus.craftgui.GUIFastCraft;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
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

    public GUIButtonMultiplier(GUIFastCraft gui) {
        this.gui = gui;

        multOrder = FastCraftPlus.config().toolbar_multiplierOrder();
        min = multOrder[0];
        max = multOrder[multOrder.length - 1];
    }

    @Override
    public ItemStack getItem() {
        int mult = gui.getMultiplier();
        ItemStack result = new ItemStack(Material.ANVIL, mult);
        if (mult > 64) result.setAmount(-1);

        // Set display name and lore
        ItemMeta meta = result.getItemMeta();
        meta.setDisplayName(FastCraftPlus.lang().gui_toolbar_multiplier_title(mult));
        meta.setLore(FastCraftPlus.lang().gui_toolbar_multiplier_description(mult));
        result.setItemMeta(meta);

        return result;
    }

    @Override
    public boolean isVisible() {
        return true;
    }

    @Override
    public boolean onClick(GUI gui, InventoryClickEvent e) {
        int mult = this.gui.getMultiplier();

        // If left click, increase. Otherwise, decrease.
        if (e.getClick() == ClickType.MIDDLE) {
            // If middle click, reset to 1
            mult = 1;
        } else if (e.isShiftClick()) {
            // If shift click, increment by 1
            mult += e.isLeftClick() ? 1 : -1;
        } else {
            // If normal click, increment to the next power of 2
            int newMult;
            if (e.isLeftClick()) {
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

        // Set new multiplier, wrapping around if necessary
        if (!e.isShiftClick()) {
            if (mult < min) {
                mult = max;
            } else if (mult > max) {
                mult = min;
            }
        }

        // Don't let multiplier go below 1
        mult = Math.max(1, mult);

        this.gui.setMultiplier(mult);
        this.gui.updateLayout();

        return true;
    }
}
