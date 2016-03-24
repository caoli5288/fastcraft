package co.kepler.fastcraftplus.craftgui;

import co.kepler.fastcraftplus.crafting.FastRecipe;
import co.kepler.fastcraftplus.crafting.Ingredient;
import co.kepler.fastcraftplus.gui.GUIButton;
import co.kepler.fastcraftplus.gui.Layout;
import org.bukkit.ChatColor;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

/**
 * A button that will encapsulate a crafting recipe.
 */
public class GUIButtonRecipe extends GUIButton {
    private static Set<ClickType> ignoreClicks = new HashSet<>(Arrays.asList(
            ClickType.CREATIVE, ClickType.DOUBLE_CLICK, ClickType.MIDDLE, ClickType.NUMBER_KEY,
            ClickType.UNKNOWN, ClickType.WINDOW_BORDER_LEFT, ClickType.WINDOW_BORDER_RIGHT
    ));

    FastRecipe recipe;
    private GUIFastCraft gui;

    /**
     * Create a new Recipe Button from a GUI and a FastRecipe.
     *
     * @param gui    The FastCraft GUI that this button is contained in.
     * @param recipe The recipe that this button will craft.
     */
    public GUIButtonRecipe(GUIFastCraft gui, FastRecipe recipe) {
        super();
        this.gui = gui;
        this.recipe = recipe;

        // Add the ingredients to the lore of the item
        ItemStack item = recipe.getResult().clone();
        ItemMeta meta = item.getItemMeta();
        LinkedList<String> lore = new LinkedList<>();
        Map<Ingredient, Integer> ingredients = recipe.getIngredients();

        // Add ingredients and amounts to the lore
        lore.addFirst(ChatColor.GREEN + "Ingredients:");
        for (Ingredient i : ingredients.keySet()) {
            // Format: #x Ingredient
            lore.addLast(ChatColor.GREEN + " " + ingredients.get(i) + "x " + i.getName());
        }

        // If the item has a lore alread, add a space between the ingredients and the existing lore
        if (meta.getLore() != null && !meta.getLore().isEmpty()) {
            lore.addFirst("");
            lore.addAll(0, meta.getLore());
        }
        meta.setLore(lore);
        item.setItemMeta(meta);

        // Set the item
        setItem(item);
    }

    /**
     * Unsupported
     *
     * @param clickAction The click action to be run when the button is clicked.
     */
    @Override
    public void setClickAction(ClickAction clickAction) {
        throw new UnsupportedOperationException();
    }

    /**
     * See if the button is visible.
     *
     * @return Returns true if the player's inventory has the necessary items to craft this recipe.
     */
    @Override
    public boolean isVisible() {
        return recipe.canCraft(gui.getPlayer(), false);
    }

    /**
     * Unsupported
     *
     * @param visible Returns true if the button is visible.
     */
    @Override
    public void setVisible(boolean visible) {
        throw new UnsupportedOperationException();
    }

    /**
     * Crafts the recipe associated with this button using the player's items.
     *
     * @param layout   The layout in which the button was clicked.
     * @param invEvent The inventory event triggered by the click.
     */
    @Override
    public boolean onClick(Layout layout, InventoryClickEvent invEvent) {
        if (ignoreClicks.contains(invEvent.getClick())) return false;
        if (!recipe.canCraft(gui.getPlayer(), true)) {
            gui.updateLayout();
            return false;
        }

        switch (invEvent.getClick()) {
            case DROP:
            case CONTROL_DROP:
                // Drop items on the ground.
                for (ItemStack is : recipe.getResults()) {
                    invEvent.getView().setItem(InventoryView.OUTSIDE, is);
                }
                break;
            default:
                // Add to inventory. Drop rest on ground if not enough space.
                Inventory inv = gui.getPlayer().getInventory();
                for (ItemStack is : inv.addItem(recipe.getResults()).values()) {
                    invEvent.getView().setItem(InventoryView.OUTSIDE, is);
                }
                break;
        }

        gui.updateLayout();
        return true;
    }


}
