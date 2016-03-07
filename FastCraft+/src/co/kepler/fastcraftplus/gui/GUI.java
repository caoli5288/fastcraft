package co.kepler.fastcraftplus.gui;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;

import co.kepler.fastcraftplus.FastCraft;
import net.md_5.bungee.api.chat.TextComponent;

/**
 * An inventory GUI.
 * GUI.disposeAll() must be executed when the plugin is disabled.
 */
public class GUI implements InventoryHolder {
	private static Set<GUI> guis = new HashSet<GUI>();
	private static boolean listenersRegistered = false;
	
	private Inventory inv;
	private GUILayout buttonLayout = null;

	/**
	 * Create a new inventory GUI.
	 * @param title The title of the GUI.
	 * @param height The height of the GUI.
	 */
	public GUI(String title, int height) {
		assert height >= 1 && height <= 6;
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
	 * Set the button layout of the GUI.
	 * @param buttonLayout The button layout to set.
	 */
	public void setButtonLayout(GUILayout buttonLayout) {
		this.buttonLayout = buttonLayout;
		buttonLayout.apply(inv);
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
		//Bukkit.getPluginManager().
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

	public class Listener implements org.bukkit.event.Listener {
		/**
		 * Handles inventory clicks on the server.
		 * @param e The inventory click event.
		 */
		@EventHandler(priority=EventPriority.LOWEST)
		public void onInventoryClick(InventoryClickEvent e) {
			GUI gui = GUI.getGUI(e.getInventory());
			if (e.isCancelled() || gui == null) return;
			
			if (e.getInventory() == e.getClickedInventory()) {
				// If the GUI was clicked...
				e.setCancelled(true);
				
				GUIButton button = buttonLayout.getButton(e.getSlot());
				if (button != null) {
					button.onClick(gui, e);
				}
			} else {
				switch (e.getClick()) {
				case SHIFT_LEFT:
				case SHIFT_RIGHT:
				case UNKNOWN:
					e.setCancelled(true);
				default:
				}
			}
		}
		
		/**
		 * Handles inventory drags on the server.
		 * Cancels the event if items are dragged into the GUI.
		 * @param e The inventory drag event.
		 */
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
	}
}
