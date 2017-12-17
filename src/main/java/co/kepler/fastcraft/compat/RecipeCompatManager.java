package co.kepler.fastcraft.compat;

import co.kepler.fastcraft.FastCraft;
import co.kepler.fastcraft.recipes.FastRecipe;
import co.kepler.fastcraft.util.RecipeUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Manages the plugin's FastRecipes.
 */
public class RecipeCompatManager {
    private List<Compat> loadedCompats = new ArrayList<>();
    private Set<Integer> handledRecipeHashes = new HashSet<>();

    /**
     * Create a new instance of RecipeCompatManager.
     */
    public RecipeCompatManager() {
        // Load plugin compatibilities
        loadCompat(new Compat_FastCraft(this));
        loadCompat(new Compat_ItemMakerPro(this));
        loadCompat(new Compat_ProRecipes(this));
        loadCompat(new Compat_Bukkit(this)); // Must be loaded last
    }

    /**
     * Load a compatibility with a plugin.
     *
     * @param compat The plugin compatibility to load.
     */
    private void loadCompat(Compat compat) {
        String pluginName = compat.dependsOnPlugin();
        Plugin plugin = null;
        if (pluginName != null) {
            plugin = Bukkit.getPluginManager().getPlugin(compat.dependsOnPlugin());
            if (plugin == null) return;
        }

        try {
            if (compat.init()) {
                loadedCompats.add(compat);
                compat.getRecipes(null);
                if (plugin != null) {
                    FastCraft.log("Loaded compatibility: " + plugin.getName());
                }
            } else {
                if (plugin != null) {
                    FastCraft.err("Unable to load compatibility: " + plugin.getName());
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    /**
     * Get all recipes loaded by compatibility classes.
     *
     * @param player The player who will be seeing the recipes.
     * @return Returns all recipes loaded by compatibility classes.
     */
    public Set<FastRecipe> getRecipes(Player player) {
        Set<FastRecipe> result = new HashSet<>();
        for (Compat compat : loadedCompats) {
            try {
                Set<FastRecipe> recipes = compat.getRecipes(player);
                if (recipes != null) result.addAll(recipes);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
        return result;
    }

    public void addHandledRecipe(int hash) {
        handledRecipeHashes.add(hash);
    }

    public void addHandledRecipe(Recipe recipe) {
        addHandledRecipe(RecipeUtil.hashRecipe(recipe));
    }

    public boolean isRecipeHandled(int hash) {
        return handledRecipeHashes.contains(hash);
    }

    public boolean isRecipeHandled(Recipe recipe) {
        return isRecipeHandled(RecipeUtil.hashRecipe(recipe));
    }
}
