package co.kepler.fastcraftplus;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import co.kepler.fastcraftplus.gui.Gui;
import co.kepler.fastcraftplus.gui.GuiButton;
import co.kepler.fastcraftplus.gui.GuiButtonClickEvent;
import co.kepler.fastcraftplus.gui.GuiLayout;

public class TestListener implements Listener {

	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent e) {
		ItemStack buttonItem = new ItemStack(Material.NETHER_STAR);
		ItemMeta buttonItemMeta = buttonItem.getItemMeta();
		buttonItemMeta.setDisplayName("THIS IS BUTTON");
		buttonItem.setItemMeta(buttonItemMeta);
		
		GuiButton starButton = new GuiButton(buttonItem);
		
		GuiLayout layout = new GuiLayout();
		layout.setButton(starButton, 3, 3);
		
		Gui gui = new Gui("TEST", 5);
		gui.setButtonLayout(layout);
		gui.show(e.getPlayer());
	}
	
	@EventHandler
	public void onButtonClick(GuiButtonClickEvent e) {
		Bukkit.broadcastMessage("Button pressed!");
	}
}
