package co.kepler.fastcraftplus.craftgui;

import co.kepler.fastcraftplus.FastCraft;
import co.kepler.fastcraftplus.api.gui.GUIButton;
import co.kepler.fastcraftplus.api.gui.Layout;
import co.kepler.fastcraftplus.config.LanguageConfig;
import co.kepler.fastcraftplus.recipes.FastRecipe;
import co.kepler.fastcraftplus.recipes.Ingredient;
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

    private FastRecipe recipe;
    private GUIFastCraft gui;

    /**
     * Create a new Recipe Button from a GUI and a GUIRecipe.
     *
     * @param gui    The FastCraft GUI that this button is contained in.
     * @param recipe The recipe that this button will craft.
     */
    public GUIButtonRecipe(GUIFastCraft gui, FastRecipe recipe) {
        super();
        this.gui = gui;
        this.recipe = recipe;
    }

    @Override
    public ItemStack getItem() {
        LanguageConfig lang = FastCraft.lang();

        // Add the ingredients to the lore of the item
        ItemStack item = recipe.getDisplayResult().clone();
        List<ItemStack> results = recipe.getResults();
        ItemMeta meta = item.getItemMeta();
        LinkedList<String> lore = new LinkedList<>();
        Map<Ingredient, Integer> ingredients = recipe.getIngredients();

        // Set the display name of the item
        meta.setDisplayName(lang.gui_itemName(item));

        // Add ingredients and amounts to the lore
        lore.addFirst(lang.gui_ingredients_label());
        for (Ingredient i : ingredients.keySet()) {
            lore.addLast(lang.gui_ingredients_item(ingredients.get(i), i.getName()));
        }

        // Add results and amounts to the lore if more than one result
        if (results.size() > 1) {
            lore.addLast("");
            lore.add(lang.gui_results_label());
            for (ItemStack is : results) {
                lore.addLast(lang.gui_results_item(is));
            }
        }

        // Add the hashcode to the lore
        if (gui.showHashes()) {
            lore.addLast("");
            lore.addAll(lang.gui_hashcode(recipe));
        }

        // If the item has a lore already, or has enchants, add an empty line
        if ((meta.getLore() != null && !meta.getLore().isEmpty()) || meta.hasEnchants()) {
            lore.addFirst("");
            lore.addAll(0, meta.getLore());
        }
        meta.setLore(lore);
        item.setItemMeta(meta);

        // Return the item
        return item;
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
        ItemStack[] contents = gui.getPlayer().getInventory().getContents();
        return recipe.removeIngredients(contents);
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

        // Craft the items, and return if unsuccessful
        Set<ItemStack> results = recipe.craft(gui);
        if (results == null) {
            gui.updateLayout();
            return false;
        }

        // Give the player the result items
        switch (invEvent.getClick()) {
        case DROP:
        case CONTROL_DROP:
            // Drop items on the ground.
            for (ItemStack is : results) {
                invEvent.getView().setItem(InventoryView.OUTSIDE, is);
            }
            break;
        default:
            // Add to inventory. Drop rest on ground if not enough space.
            Inventory inv = gui.getPlayer().getInventory();
            ItemStack[] resultsArr = new ItemStack[results.size()];
            for (ItemStack is : inv.addItem(results.toArray(resultsArr)).values()) {
                invEvent.getView().setItem(InventoryView.OUTSIDE, is);
            }
            break;
        }

        gui.updateLayout();
        return true;
    }


}
