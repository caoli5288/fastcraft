package co.kepler.fastcraftplus.compat;

import co.kepler.fastcraftplus.recipes.FastRecipe;
import com.kirelcodes.ItemMaker.API.RecipeGetter;
import com.kirelcodes.ItemMaker.Recipes.Perfect.PerfectShapedRecipe;
import org.bukkit.entity.Player;

import java.util.Set;

/**
 * Recipe compatibility for Item Maker Pro.
 * <p>
 * Plugin: https://www.spigotmc.org/resources/7173/
 */
public class Compat_ItemMakerPro extends Compat {

    @Override
    public boolean init() {
        return true;
    }

    @Override
    public String dependsOnPlugin() {
        return "ItemMakerPro";
    }

    @Override
    public Set<FastRecipe> getRecipes(Player player) {
        /*
        for (PerfectShapedRecipe recipe : RecipeGetter.getShapedRecipes()) {
            //recipe.
        }
        for (Perfe recipe : RecipeGetter.getShapelessRecipe()) {
            //recipe.
        }
        */
        return null;
    }
}
