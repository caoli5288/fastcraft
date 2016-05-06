package co.kepler.fastcraftplus.api.gui;

import co.kepler.fastcraftplus.craftgui.LayoutManager;
import org.bukkit.inventory.Inventory;

/**
 * An API that can be used by other plugins to interact with FastCraft+.
 */
public class FastCraftAPI {

    /**
     * See if an inventory is a GUI.
     *
     * @param inv The inventory to check.
     * @return Returns true if the inventory is a GUI.
     */
    public boolean isFastCraftGUI(Inventory inv) {
        return inv.getHolder() instanceof GUI;
    }

    /**
     * Get the layout manager used by FastCraft+ interfaces. The layout manager
     * controls the layout of buttons in the GUI.
     *
     * @return Returns the FastCraft+ layout manager.
     */
    public LayoutManager getLayoutManager() {
        return LayoutManager.getLayoutManager();
    }

    /**
     * Set the layout manager used by FastCraft+ interfaces. The layout manager
     * controls the layout of buttons in the GUI.
     */
    public void setLayoutManager(LayoutManager layoutManager) {
        LayoutManager.setLayoutManager(layoutManager);
    }
}
