package co.kepler.fastcraftplus.gui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;

import co.kepler.fastcraftplus.FastCraft;
import net.md_5.bungee.api.chat.TextComponent;

public class Gui implements Listener {
	private Inventory inv;
	private GuiLayout buttonLayout = null;
	
	/**
	 * Create a new inventory GUI.
	 * @param title The title of the GUI.
	 * @param height The height of the GUI.
	 */
	public Gui(String title, int height) {
		assert height >= 1 && height <= 6;
		inv = Bukkit.createInventory(null, height * 9, title);
		
		Bukkit.getPluginManager().registerEvents(this, FastCraft.getInstance());
	}
	
	/**
	 * Create a new inventory GUI.
	 * @param title The title of the GUI.
	 * @param height The height of the GUI.
	 */
	public Gui(TextComponent title, int height) {
		this(title.toLegacyText(), height);
	}
	
	/**
	 * Set the button layout of the GUI.
	 * @param buttonLayout The button layout to set.
	 */
	public void setButtonLayout(GuiLayout buttonLayout) {
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
	 * Handles inventory clicks on the server.
	 * @param e The inventory click event.
	 */
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		if (!invEq(inv, e.getInventory())) return;
		e.setCancelled(true);
		
		GuiButton button = buttonLayout.getButton(e.getSlot());
		GuiButtonClickEvent clickEvent = new GuiButtonClickEvent(button, this, e);
		Bukkit.getPluginManager().callEvent(clickEvent);
	}
	
	/**
	 * Handles inventory drags on the server.
	 * @param e The inventory drag event.
	 */
	@EventHandler
	public void onInventoryDrag(InventoryDragEvent e) {
		if (!invEq(inv, e.getInventory())) return;
		e.setCancelled(true);
	}
	
	/**
	 * Compare two inventories to see if they're equal.
	 * @param invA The first inventory to compare.
	 * @param invB The second inventory to compare.
	 * @return Return true if the viewers of the inventories are equal.
	 */
	private static boolean invEq(Inventory invA, Inventory invB) {
		return invA.getViewers().equals(invB.getViewers());
	}
}
