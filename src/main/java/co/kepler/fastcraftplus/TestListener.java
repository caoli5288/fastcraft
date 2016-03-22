package co.kepler.fastcraftplus;

import co.kepler.fastcraftplus.craftgui.GUIFastCraft;
import co.kepler.fastcraftplus.crafting.FastRecipe;
import co.kepler.fastcraftplus.crafting.Ingredient;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Just a class for all things testing related.
 */
public class TestListener implements Listener {

    public TestListener() {
        Set<FastRecipe> recipes = new HashSet<>();

        ItemStack is = new ItemStack(Material.ARROW);
        FastRecipe a = new FastRecipe(Bukkit.getRecipesFor(is).get(0));
        FastRecipe b = new FastRecipe(Bukkit.getRecipesFor(is).get(0));
        recipes.add(a);
        System.out.println("-------TEST: " + (recipes.contains(b)));

        Set<Ingredient> ingredients = new HashSet<>();
        Ingredient c = new Ingredient(new ItemStack(Material.ACACIA_DOOR));
        Ingredient d = new Ingredient(new ItemStack(Material.ACACIA_DOOR));
        ingredients.add(c);
        System.out.println("TEST 2: " + c.equals(d) + ", " + ingredients.contains(d));
        System.out.println(c.hashCode() + ", " + d.hashCode());


        ItemStack e = new ItemStack(Material.APPLE);
        ItemStack f = new ItemStack(Material.APPLE);
        System.out.println(e.hashCode() + ", " + f.hashCode());
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        e.getPlayer().getInventory().setItem(0, new ItemStack(Material.GOLD_INGOT, 64));

        GUIFastCraft fcGUI = new GUIFastCraft(e.getPlayer(), null);
        fcGUI.show();
    }
}
