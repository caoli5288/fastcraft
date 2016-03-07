package co.kepler.fastcraftplus;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import co.kepler.fastcraftplus.gui.GUI;
import co.kepler.fastcraftplus.gui.GUIButton;
import co.kepler.fastcraftplus.gui.GUILayout;

public class TestListener implements Listener {

	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent e) {
		ItemStack buttonItem = new ItemStack(Material.NETHER_STAR);
		ItemMeta buttonItemMeta = buttonItem.getItemMeta();
		buttonItemMeta.setDisplayName("THIS IS BUTTON");
		buttonItem.setItemMeta(buttonItemMeta);
		
		GUIButton starButton = new GUIButton(buttonItem) {
			@Override
			public void onClick(GUI gui, InventoryClickEvent invEvent) {
				onButtonClick(gui, invEvent);
			}
		};
		
		GUILayout layout = new GUILayout();
		layout.setButton(3, 3, starButton);
		
		GUI gui = new GUI("TEST", 5);
		gui.setButtonLayout(layout);
		gui.show(e.getPlayer());
	}
	
	public void onButtonClick(GUI gui, InventoryClickEvent invEvent) {
		Bukkit.broadcastMessage("Clicked!");
	}
}
