package co.kepler.fastcraftplus;

import co.kepler.fastcraftplus.craftgui.GUIFastCraft;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import java.util.List;

/**
 * Just a class for all things testing related.
 */
public class TestListener implements Listener {

    public TestListener() {
        ItemStack banner = new ItemStack(Material.BANNER);
        List<Recipe> recipes = Bukkit.getRecipesFor(banner);
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        e.getPlayer().getInventory().setItem(0, new ItemStack(Material.GOLD_INGOT, 64));

        GUIFastCraft fcGUI = new GUIFastCraft(e.getPlayer(), null);
        fcGUI.show();
    }
}
