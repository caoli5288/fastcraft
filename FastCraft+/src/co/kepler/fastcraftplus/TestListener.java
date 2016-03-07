package co.kepler.fastcraftplus;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import co.kepler.fastcraftplus.gui.GUI;
import co.kepler.fastcraftplus.gui.GUIButton;
import co.kepler.fastcraftplus.gui.GUILayout;

public class TestListener implements Listener {

    public TestListener() {
        ItemStack banner = new ItemStack(Material.BANNER);
        List<Recipe> recipes = Bukkit.getRecipesFor(banner);

        for (Recipe r : recipes) {
            System.out.println("---------------- " + r);
            if (r instanceof ShapedRecipe) {
                ShapedRecipe sr = (ShapedRecipe) r;
                System.out.println(sr.getIngredientMap());
                for (Character c : sr.getIngredientMap().keySet()) {
                    System.out.println(c + ": " + sr.getIngredientMap().get(c));
                }
            } else if (r instanceof ShapelessRecipe) {
                ShapelessRecipe sr = (ShapelessRecipe) r;
                System.out.println(sr.getIngredientList());
            }
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        GUI gui = new GUI("TEST", 5);

        ItemStack buttonItem = new ItemStack(Material.NETHER_STAR);
        ItemMeta buttonItemMeta = buttonItem.getItemMeta();
        buttonItemMeta.setDisplayName("THIS IS BUTTON");
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

        GUILayout layout = new GUILayout(gui);
        layout.setPendingButton(3, 3, starButton);
        layout.updateGUI();

        gui.show(e.getPlayer());
    }
}
