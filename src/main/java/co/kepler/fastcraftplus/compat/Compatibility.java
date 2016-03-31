package co.kepler.fastcraftplus.compat;

import co.kepler.fastcraftplus.FastCraft;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages plugin compatibilities
 */
public class Compatibility {
    private List<PluginCompat> loadedCompats;

    /**
     * Create a new instance of Compatibility.
     */
    public Compatibility() {
        loadedCompats = new ArrayList<>();

        // Load plugin compatibilities
        loadCompat(new CompatItemMakerPro());
        loadCompat(new CompatProRecipes());
    }

    /**
     * Load a compatibility with a plugin.
     *
     * @param pluginCompat The plugin compatibility to load.
     */
    private void loadCompat(PluginCompat pluginCompat) {
        Plugin plugin = Bukkit.getPluginManager().getPlugin(pluginCompat.getPluginName());
        if (plugin != null) {
            pluginCompat.init();
            loadedCompats.add(pluginCompat);
            FastCraft.log("Loaded compatibility: " + plugin.getName());
        }
    }
}
