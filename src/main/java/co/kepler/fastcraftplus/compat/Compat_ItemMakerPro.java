package co.kepler.fastcraftplus.compat;

import co.kepler.fastcraftplus.recipes.FastRecipe;

import java.util.Set;

/**
 * Recipe compatibility for Item Maker Pro.
 * Plugin: https://www.spigotmc.org/resources/7173/
 */
public class Compat_ItemMakerPro extends Compat {

    @Override
    public boolean init() {
        return true;
    }

    @Override
    public String getPluginName() {
        return "ItemMakerPro";
    }

    @Override
    public Set<FastRecipe> getRecipes() {
        return null;
    }
}
