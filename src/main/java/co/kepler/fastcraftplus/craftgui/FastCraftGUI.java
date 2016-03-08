package co.kepler.fastcraftplus.craftgui;

import co.kepler.fastcraftplus.gui.GUI;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

/**
 * The FastCraft crafting GUI.
 */
public class FastCraftGUI extends GUI {

    /**
     * Create a new instance of a FastCraft GUI.
     * @param player The player who will be shown this GUI.
     */
    public FastCraftGUI(Player player) {
        super("FastCraft", 6); // TODO Localize
    }

    @Override
    public void onClose(HumanEntity closedBy) {
        if (getInventory().getViewers().isEmpty()) {
            dispose();
        }
    }
}
