package co.kepler.fastcraftplus;

import co.kepler.fastcraftplus.gui.GUI;
import co.kepler.fastcraftplus.gui.GUIButton;
import co.kepler.fastcraftplus.gui.GUILayout;
import co.kepler.fastcraftplus.gui.GUIPagedLayout;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.ItemMeta;

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
        GUI gui = new GUI("TEST", 6);


        ItemStack prevItem = new ItemStack(Material.FEATHER);
        ItemMeta prevItemMeta = prevItem.getItemMeta();
        prevItemMeta.setDisplayName("PREV PAGE");
        prevItem.setItemMeta(prevItemMeta);

        ItemStack nextItem = new ItemStack(Material.FEATHER);
        ItemMeta nextItemMeta = nextItem.getItemMeta();
        nextItemMeta.setDisplayName("§cNEXT PAGE");
        nextItem.setItemMeta(nextItemMeta);

        GUIPagedLayout layout = new GUIPagedLayout(gui, GUIPagedLayout.NavPosition.BOTTOM, 1);
        for (int i = 0; i < 22; i++) {
            ItemStack buttonItem = new ItemStack(Material.NETHER_STAR);
            ItemMeta buttonItemMeta = buttonItem.getItemMeta();
            buttonItemMeta.setDisplayName("THIS IS BUTTON (" + i + ")");
            buttonItem.setItemMeta(buttonItemMeta);
            GUIButton starButton = new GUIButton(buttonItem) {
                @Override
                public void onClick(GUILayout layout, InventoryClickEvent invEvent) {
                    Bukkit.broadcastMessage("Clicked!");
                }

                @Override
                public boolean isVisible(GUILayout layout) {
                    return true;
                }
            };

            layout.setPendingButton(i, i % 9, starButton);
        }
        layout.setNavButton(0, new GUIPagedLayout.GUIButtonPrevPage(prevItem));
        layout.setNavButton(8, new GUIPagedLayout.GUIButtonNextPage(nextItem));

        layout.updateGUI();

        gui.show(e.getPlayer());
    }
}
