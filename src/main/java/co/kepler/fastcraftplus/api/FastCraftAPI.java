package co.kepler.fastcraftplus.api;

import co.kepler.fastcraftplus.api.gui.GUI;
import co.kepler.fastcraftplus.craftgui.GUIFastCraft;
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
    public static boolean isGUI(Inventory inv) {
        return inv.getHolder() instanceof GUI;
    }

    /**
     * See if an inventory is a FastCraft+ GUI.
     *
     * @param inv The inventory to check.
     * @return Returns true if the inventory is a FastCraft+ GUI.
     */
    public static boolean isFastCraftGUI(Inventory inv) {
        return inv.getHolder() instanceof GUIFastCraft;
    }

    /**
     * Get the layout manager used by FastCraft+ interfaces. The layout manager
     * controls the layout of buttons in the GUI.
     *
     * @return Returns the FastCraft+ layout manager.
     */
    public static LayoutManager getLayoutManager() {
        return LayoutManager.getLayoutManager();
    }

    /**
     * Set the layout manager used by FastCraft+ interfaces. The layout manager
     * controls the layout of buttons in the GUI.
     */
    public static void setLayoutManager(LayoutManager layoutManager) {
        LayoutManager.setLayoutManager(layoutManager);
    }
}
