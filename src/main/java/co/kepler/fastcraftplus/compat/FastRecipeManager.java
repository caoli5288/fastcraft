package co.kepler.fastcraftplus.compat;

import co.kepler.fastcraftplus.FastCraft;
import co.kepler.fastcraftplus.recipes.FastRecipe;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Manages the plugin's FastRecipes.
 */
public class FastRecipeManager {
    private List<Compat> loadedCompats;

    /**
     * Create a new instance of FastRecipeManager.
     */
    public FastRecipeManager() {
        loadedCompats = new ArrayList<>();

        // Load plugin compatibilities
        loadCompat(new Compat_ItemMakerPro());
        loadCompat(new Compat_ProRecipes());
    }

    /**
     * Load a compatibility with a plugin.
     *
     * @param compat The plugin compatibility to load.
     */
    private void loadCompat(Compat compat) {
        String pluginName = compat.getPluginName();
        Plugin plugin = null;
        if (pluginName != null) {
            plugin = Bukkit.getPluginManager().getPlugin(compat.getPluginName());
            if (plugin == null) return;
        }

        if (compat.init()) {
            loadedCompats.add(compat);
            if (plugin != null) {
                FastCraft.log("Loaded compatibility: " + plugin.getName());
            }
        } else {
            if (plugin != null) {
                FastCraft.err("Unable to load compatibility: " + plugin.getName());
            }
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
            Set<FastRecipe> recipes = compat.getRecipes(player);
            if (recipes != null) result.addAll(recipes);
        }
        return result;
    }
}
