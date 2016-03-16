package co.kepler.fastcraftplus;

import co.kepler.fastcraftplus.craftgui.GUIFastCraft;
import co.kepler.fastcraftplus.gui.GUIButtonGlowing;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
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
        e.getPlayer().getInventory().getContents()[5] = new ItemStack(Material.APPLE, 42);

        GUIFastCraft fcGUI = new GUIFastCraft(e.getPlayer(), null);
        fcGUI.show();

        GUIButtonGlowing button = new GUIButtonGlowing(new ItemStack(Material.FEATHER));
        button.setGlowing(true);
        e.getPlayer().getInventory().addItem(new ItemStack(button.getItem()));

        ItemMeta meta = new ItemStack(Material.COOKED_BEEF).getItemMeta();
        meta.setDisplayName("TEST");
        System.out.println("EQUALS: " + Bukkit.getItemFactory().equals(null, meta));

        Recipe r = Bukkit.getRecipesFor(new ItemStack(Material.STICK)).get(0);
        if (r instanceof ShapedRecipe) {
            for (ItemStack is : ((ShapedRecipe) r).getIngredientMap().values()) {
                System.out.println(is.getData());
            }
        } else if (r instanceof ShapelessRecipe) {
            for (ItemStack is : ((ShapelessRecipe) r).getIngredientList()) {
                System.out.println(is.getData());
            }
        }
    }
}
