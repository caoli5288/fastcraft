package co.kepler.fastcraft.craftgui;

import co.kepler.fastcraft.FastCraft;
import co.kepler.fastcraft.Permissions;
import co.kepler.fastcraft.util.BukkitUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manages players preferences, and handles opening crafting GUI's.
 */
public class PlayerManager implements Listener {

    /**
     * Handles player interactions with workbench blocks.
     *
     * @param e The player interact event.
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.isCancelled()) return;
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (e.getClickedBlock().getType() != Material.WORKBENCH) return;
        if (e.getPlayer().isSneaking()) return;

        // See if the player has permission to open a FastCraft interface
        Player player = e.getPlayer();
        if (!player.hasPermission(Permissions.USE)) return;

        // Cancel the event, and open the GUI
        Location loc = e.getClickedBlock().getLocation();
        Bukkit.getScheduler().runTaskLater(FastCraft.getInstance(), () -> {
            player.sendBlockChange(loc, Material.AIR, (byte) 0x0);
            Bukkit.getScheduler().runTaskLater(FastCraft.getInstance(), () -> {
                new GUIFastCraft(player, e.getClickedBlock().getLocation(), false).show();
                Bukkit.getScheduler().runTask(FastCraft.getInstance(), () -> player.sendBlockChange(loc, Material.WORKBENCH, (byte) 0x0));
            }, 5);
        }, 5);

        e.setCancelled(true);
    }

}
