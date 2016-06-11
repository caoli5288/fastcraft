package co.kepler.fastcraftplus.craftgui.buttons;

import co.kepler.fastcraftplus.FastCraft;
import co.kepler.fastcraftplus.api.gui.GUI;
import co.kepler.fastcraftplus.api.gui.GUIButton;
import co.kepler.fastcraftplus.config.LanguageConfig;
import co.kepler.fastcraftplus.craftgui.GUIFastCraft;
import co.kepler.fastcraftplus.recipes.FastRecipe;
import co.kepler.fastcraftplus.recipes.Ingredient;
import co.kepler.fastcraftplus.util.BukkitUtil;
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
        this.gui = gui;
        this.recipe = recipe;
    }

    @Override
    public ItemStack getItem() {
        LanguageConfig lang = FastCraft.lang();
        int mult = gui.getMultiplier();

        // Add the ingredients to the lore of the item
        ItemStack item = recipe.getDisplayResult().clone();
        item.setAmount(item.getAmount() * mult);
        List<ItemStack> results = recipe.getResults();
        ItemMeta meta = item.getItemMeta();
        List<Ingredient> ingredients = recipe.getIngredients();

        // Set the display name of the item
        meta.setDisplayName(lang.gui_itemName(item));

        // Get the lore
        List<String> lore = meta.getLore();
        if (lore == null) {
            lore = new ArrayList<>();
        }

        // Add a blank line if the item has a lore/enchants
        if (!lore.isEmpty() || meta.hasEnchants()) {
            lore.add(null);
        }

        // Add ingredients and amounts to the lore
        lore.add(lang.gui_ingredients_label());
        for (Ingredient i : ingredients) {
            lore.add(lang.gui_ingredients_item(i.getAmount() * mult, i.getName()));
        }

        // Add results and amounts to the lore if more than one result
        if (results.size() > 1 || item.getAmount() > 64) {
            if (item.getAmount() > 64) item.setAmount(0);
            lore.add(null);
            lore.add(lang.gui_results_label());
            for (ItemStack is : results) {
                lore.add(lang.gui_results_item(is.getAmount() * mult, BukkitUtil.getItemName(is)));
            }
        }

        // Add the hashcode to the lore
        if (gui.showHashes()) {
            lore.add(null);
            lore.addAll(lang.gui_hashcode(recipe));
        }

        // Set the new lore, and return the item
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    /**
     * See if the button is visible.
     *
     * @return Returns true if the player's inventory has the necessary items to craft this recipe.
     */
    @Override
    public boolean isVisible() {
        int amount = recipe.getDisplayResult().getAmount() * gui.getMultiplier();

        ItemStack[] contents = gui.getPlayer().getInventory().getContents();
        return recipe.removeIngredients(contents, gui.getMultiplier());
    }

    @Override
    public boolean onClick(GUI g, InventoryClickEvent invEvent) {
        if (ignoreClicks.contains(invEvent.getClick())) return false;

        // Craft the items, and return if unsuccessful
        List<ItemStack> results = recipe.craft(gui, gui.getMultiplier());
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
            Inventory inv = this.gui.getPlayer().getInventory();
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
