package co.kepler.fastcraftplus.gui;

import co.kepler.fastcraftplus.FastCraft;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * An inventory GUI.
 * GUI.disposeAll() must be executed when the plugin is disabled to avoid item duplication.
 */
public class GUI implements InventoryHolder {
	private static Set<GUI> guis = new HashSet<>();
	private static boolean listenersRegistered = false;

	private final int height;

	private GUILayout layout = null;
	private Inventory inv;

	/**
	 * Create a new inventory GUI.
	 * @param title The title of the GUI.
	 * @param height The height of the GUI.
	 */
	public GUI(String title, int height) {
		assert height >= 1 && height <= 6 : "Height (" + height + ") must be from 1 to 6";
		this.height = height;
		inv = Bukkit.createInventory(this, height * 9, title);
		guis.add(this);
		
		// Register the GUI listeners if they aren't already.
		if (!listenersRegistered) {
			Bukkit.getPluginManager().registerEvents(new Listener(), FastCraft.getInstance());
			listenersRegistered = true;
		}
	}

	/**
	 * Create a new inventory GUI.
	 * @param title The title of the GUI.
	 * @param height The height of the GUI.
	 */
	public GUI(TextComponent title, int height) {
		this(title.toLegacyText(), height);
	}

	/**
	 * Open the GUI for the specified players.
	 * @param players The players who will be shown the GUI.
	 */
	public void show(Player... players) {
		for (Player p : players) {
			p.openInventory(inv);
		}
	}

	/**
	 * Close the GUI, and remove it from the server.
	 */
	public void dispose() {
		// Close the GUI for all viewers.
		while (!inv.getViewers().isEmpty()) {
			inv.getViewers().get(0).closeInventory();
		}

		guis.remove(this);
	}

	/**
	 * Dispose all of the GUI's
	 */
	public static void disposeAll() {
		for (GUI gui : guis) {
			gui.dispose();
		}
	}

	@Override
	public Inventory getInventory() {
		return inv;
	}
	
	/**
	 * Gets the number of rows in the GUI.
	 * @return Returns the number of rows in the GUI.
	 */
	public int getRowCount() {
		return inv.getSize() / 9;
	}
	
	/**
	 * Set the layout of the GUI.
	 * @param layout the layout to set.
	 */
	public void setLayout(GUILayout layout) {
		this.layout = layout;
		layout.setHeight(height);
	}

    /**
     * Update the GUI's layout.
     */
    public void updateLayout() {
        // Make the pending buttons active.
        layout.activateButtons();

        // Clear the inventory, and add the GUI's buttons to the inventory.
        inv.clear();
        int invSize = inv.getSize();
        for (int i = 0; i < invSize; i++) {
            GUIButton button = layout.getButton(i);
            if (button == null || !button.isVisible(layout)) continue;
            inv.setItem(i, button.getItem());
        }
    }

    /**
     * Get the layout of the GUI.
     * @return Returns the GUI's layout.
     */
    public GUILayout getLayout() {
        return this.layout;
    }

    /**
     * Get the height in the GUI.
     * @return Returns the number of rows in the GUI.
     */
    public int getHeight() {
        return height;
    }

    /**
     * Will be called when a player closes the inventory.
     * @param closedBy The player who closed the inventory.
     */
    public void onClose(HumanEntity closedBy) {}

    /**
	 * Get the GUI that holds the given inventory.
	 * @param inv The inventory to get the GUI of.
	 * @return Returns the GUI that holds the given inventory, or null if there is none.
	 */
	public static GUI getGUI(Inventory inv) {
		InventoryHolder holder = inv.getHolder();
		if (holder instanceof GUI && guis.contains(holder)) {
			return (GUI) holder;
		}
		return null;
	}

	/**
	 * Handles all inventory events, and forwards button presses.
	 */
	public static class Listener implements org.bukkit.event.Listener {

		@EventHandler(priority=EventPriority.LOWEST)
		public void onInventoryClick(InventoryClickEvent e) {
			GUI gui = GUI.getGUI(e.getInventory());
			if (e.isCancelled() || gui == null) return;
			
			if (e.getInventory() == e.getClickedInventory()) {
				// If the GUI was clicked...
				e.setCancelled(true);

				// See if a button was clicked, and if it's visible, process the click.
				GUIButton button = gui.layout.getButton(e.getSlot());
				if (button != null && button.isVisible(gui.layout)) {
                    // Play the button's click sound, and call the button's onClick() method.
                    if (e.getWhoClicked() instanceof Player) {
                        Player player = (Player) e.getWhoClicked();
                        player.playSound(player.getLocation(), button.getClickSound(), 1, 1);
                    }
					button.onClick(gui.layout, e);
				}
			} else {
                // Cancel shift clicks to stop items from being put into the GUI.
				switch (e.getClick()) {
				case SHIFT_LEFT:
				case SHIFT_RIGHT:
				case UNKNOWN:
					e.setCancelled(true);
				default:
				}
			}
		}

		@EventHandler(priority=EventPriority.LOWEST)
		public void onInventoryDrag(InventoryDragEvent e) {
			GUI gui = GUI.getGUI(e.getInventory());
			if (e.isCancelled() || gui == null) return;
			
			// Cancel if dragged into GUI.
			InventoryView view = e.getView();
			for (Integer i : e.getRawSlots()) {
				if (i == view.convertSlot(i)) {
					e.setCancelled(true);
					return;
				}
			}
		}

        @EventHandler(priority=EventPriority.LOWEST)
        public void onInventoryClose(InventoryCloseEvent e) {
            GUI gui = getGUI(e.getInventory());
            if (gui != null) {
                gui.onClose(e.getPlayer());
            }
        }
    }
}
