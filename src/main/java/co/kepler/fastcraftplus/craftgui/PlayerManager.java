package co.kepler.fastcraftplus.craftgui;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Manages players preferences, and handles opening crafting GUI's.
 */
public class PlayerManager implements Listener {









    /*
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.isCancelled()) return;
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (e.getClickedBlock().getType() != Material.WORKBENCH) return;

        // Cancel the interaction, and show the FastCraft GUI.
        e.setCancelled(true);
        new GUIFastCraft(e.getPlayer(), e.getClickedBlock().getLocation()).show();
    }
    */
}
