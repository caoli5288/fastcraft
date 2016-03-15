package co.kepler.fastcraftplus.gui;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import java.util.Arrays;

/**
 * Allows for easy creation of items to be used as GUI buttons.
 */
public class GUIItemBuilder {
    private final ItemStack base;
    private final ItemMeta meta;

    private boolean glowing = false;
    private boolean hideInfo = false;

    /**
     * Create an item builder based off of an ItemStack.
     *
     * @param base The ItemStack to build off of.
     */
    public GUIItemBuilder(ItemStack base) {
        this.base = new ItemStack(base);
        this.meta = this.base.getItemMeta();
    }

    /**
     * Create an item builder from a Material.
     *
     * @param material The Material to create the builder from.
     */
    public GUIItemBuilder(Material material) {
        this(new ItemStack(material));
    }

    /**
     * Create an item builder from MaterialData.
     *
     * @param materialData The MaterialData to create the builder from.
     */
    public GUIItemBuilder(MaterialData materialData) {
        this(materialData.toItemStack());
    }

    /**
     * Build an instance of ItemStack.
     *
     * @return Returns an instance of ItemStack.
     */
    public ItemStack build() {
        if (hideInfo) {
            // Add item tags to hide the item's info.
            meta.addItemFlags(
                    ItemFlag.HIDE_ATTRIBUTES,
                    ItemFlag.HIDE_ENCHANTS,
                    ItemFlag.HIDE_DESTROYS,
                    ItemFlag.HIDE_PLACED_ON,
                    ItemFlag.HIDE_POTION_EFFECTS,
                    ItemFlag.HIDE_UNBREAKABLE
            );
        }
        if (glowing) {
            // Only add an enchantment if one does not already exist.
            if (meta.getEnchants().isEmpty()) {
                meta.addEnchant(Enchantment.KNOCKBACK, 1, true);
            }

            // Only hide the info if hideInfo is false
            if (!hideInfo) {
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
        }
        base.setItemMeta(meta);
        return base;
    }

    /**
     * Set the display name of the item.
     *
     * @param displayName The display name of the item.
     * @return Returns this builder, so methods can be chained.
     */
    public GUIItemBuilder setDisplayName(String displayName) {
        meta.setDisplayName(displayName);
        return this;
    }

    /**
     * Set the lore of the item.
     *
     * @param lore The lore of the item.
     * @return Returns this builder, so methods can be chained.
     */
    public GUIItemBuilder setLore(String... lore) {
        meta.setLore(Arrays.asList(lore));
        return this;
    }

    /**
     * Adds an enchantment, and hides the enchantments on the item.
     *
     * @param glowing Whether the item should be glowing.
     * @return Returns this builder, so methods can be chained.
     */
    public GUIItemBuilder setGlowing(boolean glowing) {
        this.glowing = glowing;
        return this;
    }

    /**
     * Hide an item's info. (Attributes, enchants, etc.)
     *
     * @param hide Whether the info should be hidden.
     * @return Returns this builder, so methods can be chained.
     */
    public GUIItemBuilder setHideInfo(boolean hide) {
        this.hideInfo = hide;
        return this;
    }
}
