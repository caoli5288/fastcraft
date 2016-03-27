package co.kepler.fastcraftplus.recipes;

import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;

/**
 * A furnace recipe than supports input items with metadata.
 */
public class CustomFurnaceRecipe extends FurnaceRecipe {
    private ItemStack input;
    private float exp;

    public CustomFurnaceRecipe(ItemStack input, ItemStack result, float exp) {
        super(result, input.getData());
        this.input = input;
        this.exp = exp;
    }

    @Override
    public ItemStack getInput() {
        return input;
    }

    // Override (Not in 1.8 api, exists in 1.9 api)
    public float getExperience() {
        return exp;
    }

    // Override (Not in 1.8 api, exists in 1.9 api)
    public void setExperience(float exp) {
        this.exp = exp;
    }
}
