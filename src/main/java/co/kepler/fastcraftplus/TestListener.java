package co.kepler.fastcraftplus;

import co.kepler.fastcraftplus.craftgui.GUIFastCraft;
import co.kepler.fastcraftplus.gui.GUIButtonGlowing;
import co.kepler.fastcraftplus.gui.GUIItemBuilder;
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
        GUIFastCraft fcGUI = new GUIFastCraft(e.getPlayer());
        fcGUI.show();


        GUIButtonGlowing button = new GUIButtonGlowing(new ItemStack(Material.FEATHER));
        button.setGlowing(true);
        e.getPlayer().getInventory().addItem(new ItemStack(button.getItem()));
    }
}
