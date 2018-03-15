package co.kepler.fastcraft.craftgui;

import co.kepler.fastcraft.FastCraft;
import co.kepler.fastcraft.api.gui.GUI;
import co.kepler.fastcraft.api.gui.Layout;
import co.kepler.fastcraft.craftgui.layouts.LayoutFastCraft;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * The FastCraft crafting GUI.
 */
public class GUIFastCraft extends GUI {
    private static Map<Object, GUIFastCraft> guis = new HashMap<>(); // <Location or UUID, GUIFastCraft>

    private final Player player;
    private final Location location;
    private final boolean showHashes;

    private int multiplier = 1;

    /**
     * Create a new instance of a FastCraft GUI.
     *
     * @param player The player who will be shown this GUI.
     */
    public GUIFastCraft(final Player player, Location location, boolean showHashes) {
        super(FastCraft.lang().gui_title(), 6);

        this.player = player;
        this.location = location;
        this.showHashes = showHashes;

        setLayout(LayoutManager.getLayoutManager().getNewLayout(this));

        // Update the GUI's layout
        updateLayout();

        // Add to guis
        guis.put(player.getUniqueId(), this);
        guis.put(location, this);
    }

    @Override
    public void show(Player... players) {
        assert players.length == 1 && players[0].equals(player) :
                "FastCraft GUI can only be shown to its associated player";
        super.show(players);
    }

    /**
     * Open the GUIFastCraft for its associated player.
     */
    public void show() {
        show(player);
    }

    @Override
    public void onClose(HumanEntity closedBy) {
        if (getInventory().getViewers().isEmpty()) {
            dispose();
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        guis.remove(player.getUniqueId());
        guis.remove(location);
    }

    @Override
    public LayoutFastCraft getLayout() {
        return (LayoutFastCraft) super.getLayout();
    }

    @Override
    public void setLayout(Layout layout) {
        assert layout instanceof LayoutFastCraft : "Parameter layout must be an instance of LayoutFastCraft";
        super.setLayout(layout);
    }

    @Override
    public void updateLayout() {
        getLayout().getTopLayout().updateRecipes();
        super.updateLayout();
    }

    /**
     * Get the player being shown this GUI.
     *
     * @return Returns the player being shown this gui.
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Get the location of this GUI.
     *
     * @return Returns the location of this GUI.
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Get the multiplier for recipes in the interface.
     *
     * @return Returns the multiplier.
     */
    public int getMultiplier() {
        return multiplier;
    }

    /**
     * Set them multiplier for recipes in the interface.
     *
     * @param multiplier The new multiplier.
     */
    public void setMultiplier(int multiplier) {
        this.multiplier = multiplier;
    }

    /**
     * See if hashes should be shown in the GUI.
     *
     * @return Return true if hashes should be shown
     */
    public boolean showHashes() {
        return showHashes;
    }

    private void inventoryChange() {
        updateLayout();
    }

    public static class GUIListener implements Listener {
        @EventHandler(priority = EventPriority.MONITOR)
        public void onInventoryClick(InventoryClickEvent e) {
            if (!e.isCancelled()) invChange(e.getWhoClicked());
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onInventoryDrag(InventoryDragEvent e) {
            if (!e.isCancelled()) invChange(e.getWhoClicked());
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onInventoryPickup(InventoryPickupItemEvent e) {
            if (e.isCancelled()) return;
            for (HumanEntity he : e.getInventory().getViewers()) {
                invChange(he);
            }
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPlayerPickupItem(PlayerPickupItemEvent e) {
            if (!e.isCancelled()) invChange(e.getPlayer());
        }

        /**
         * Notify GUI's that the inventory has changed.
         *
         * @param player The player whose inventory was changed.
         */
        private void invChange(HumanEntity player) {
            final GUIFastCraft gui = guis.get(player.getUniqueId());
            if (gui != null) {
                FastCraft fc = FastCraft.getInstance();
                Bukkit.getScheduler().scheduleSyncDelayedTask(fc, new Runnable() {
                    public void run() {
                        gui.inventoryChange();
                    }
                }, 1L);
            }
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onBlockBreak(BlockBreakEvent e) {
            if (e.isCancelled()) return;
            blockRemoved(e.getBlock());
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onBlockBurn(BlockBurnEvent e) {
            if (e.isCancelled()) return;
            blockRemoved(e.getBlock());
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onEntityExplode(EntityExplodeEvent e) {
            if (e.isCancelled()) return;
            for (Block b : e.blockList()) {
                blockRemoved(b);
            }
        }

        private void blockRemoved(Block b) {
            if (b.getType() != Material.WORKBENCH) return;
            GUIFastCraft gui = guis.get(b.getLocation());
            if (gui != null) {
                gui.dispose();
            }
        }
    }
}
