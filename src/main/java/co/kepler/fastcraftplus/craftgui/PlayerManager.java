package co.kepler.fastcraftplus.craftgui;

import co.kepler.fastcraftplus.FastCraft;
import co.kepler.fastcraftplus.Permission;
import org.bukkit.Location;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manages players preferences, and handles opening crafting GUI's.
 */
public class PlayerManager implements Listener {
    private final Map<UUID, Integer> allowWorkbench = new HashMap<>();

    /**
     * Allow a workbench to be opened for a tick.
     *
     * @param uuid The player's UUID.
     */
    private void allowWorkbenchForTick(final UUID uuid) {
        // Allow workbench to open
        Integer val = allowWorkbench.get(uuid);
        allowWorkbench.put(uuid, (val == null ? 0 : val) + 1); // Increment, or set to 1 if null

        // Disallow workbench to open in a tick
        new BukkitRunnable() {
            public void run() {
                Integer val = allowWorkbench.get(uuid);
                if (val == null) return;
                allowWorkbench.put(uuid, --val == 0 ? null : val); // Decrement, or set null if zero
            }
        }.runTask(FastCraft.getInstance());
    }

    /**
     * See if a workbench is being allowed to open.
     *
     * @param uuid The player's UUID.
     * @return Returns true if a workbench should be allowed to open.
     */
    private boolean isWorkbenchAllowed(UUID uuid) {
        return allowWorkbench.containsKey(uuid);
    }

    /**
     * Open a workbench for a player.
     *
     * @param player   The player for which the workbench will be opened.
     * @param location The location of the workbench.
     * @param force    Whether the inventory should be forced open.
     */
    public void openWorkbench(HumanEntity player, Location location, boolean force) {
        allowWorkbenchForTick(player.getUniqueId());
        player.openWorkbench(location, force);
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent e) {
        if (e.isCancelled() || e.getInventory().getType() != InventoryType.WORKBENCH) return;

        // See if the player is able to open a FastCraft+ interface
        HumanEntity humanEntity = e.getPlayer();
        final Inventory inv = e.getInventory();
        if (!(humanEntity instanceof Player)) return;
        if (isWorkbenchAllowed(humanEntity.getUniqueId())) return;
        if (!e.getPlayer().hasPermission(Permission.USE)) return;

        // Cancel event
        e.setCancelled(true);

        // Open GUI in one tick
        final Player player = (Player) humanEntity;
        final Location location = inv.getLocation();
        new BukkitRunnable() {
            public void run() {
                new GUIFastCraft(player, location).show();
            }
        }.runTask(FastCraft.getInstance());
    }
}
