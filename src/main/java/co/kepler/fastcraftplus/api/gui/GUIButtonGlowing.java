package co.kepler.fastcraftplus.api.gui;

import org.bukkit.inventory.ItemStack;

/**
 * A GUIButton with togglable glowing.
 */
public class GUIButtonGlowing extends GUIButtonBasic {
    private ItemStack normalItem;
    private ItemStack glowingItem;
    private boolean glowing = false;

    /**
     * Create a new glowing GUIButton from an ItemStack.
     *
     * @param item The item that represents the button.
     */
    public GUIButtonGlowing(ItemStack item) {
        super(item);
        setupItems();
    }

    /**
     * Create the normal and glowing items.
     */
    private void setupItems() {
        normalItem = super.getItem();
        glowingItem = new GUIItemBuilder(super.getItem())
                .setGlowing(true).build();
    }

    @Override
    public ItemStack getItem() {
        return glowing ? glowingItem : normalItem;
    }

    /**
     * Check whether the button is glowing.
     *
     * @return Returns true if the button is glowing.
     */
    public boolean isGlowing() {
        return glowing;
    }

    /**
     * Set the glowing state of the button.
     *
     * @param glowing Whether the button should be glowing.
     */
    public void setGlowing(boolean glowing) {
        this.glowing = glowing;
    }
}
