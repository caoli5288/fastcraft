package co.kepler.fastcraftplus;

import co.kepler.fastcraftplus.api.gui.GUI;
import co.kepler.fastcraftplus.config.Config;
import co.kepler.fastcraftplus.config.Language;
import co.kepler.fastcraftplus.config.Recipes;
import co.kepler.fastcraftplus.craftgui.GUIFastCraft;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class FastCraft extends JavaPlugin implements Listener {
    private static FastCraft instance;

    private Config config;
    private Language lang;

    @Override
    public void onEnable() {
        instance = this;

        config = new Config();
        lang = new Language(config.getLanguage());
        Recipes.loadRecipes();

        GUIFastCraft.init();
    }

    @Override
    public void onDisable() {
        GUI.disposeAll();
    }

    /**
     * Get an instance of FastCraft.
     *
     * @return Returns an instance of FastCraft.
     */
    public static FastCraft getInstance() {
        return instance;
    }

    /**
     * Get the plugin's language configuration.
     *
     * @return Return the plugin's language configuration.
     */
    public static Language lang() {
        return instance.lang;
    }

    /**
     * See if this plugin is the premium version of FastCraft.
     *
     * @return Returns true if this plugin is the premium version.
     */
    public static boolean isPremium() {
        return false;
    }

    /**
     * Log an error message to the console.
     *
     * @param msg The message to log.
     */
    public static void err(String msg) {
        instance.getLogger().log(Level.SEVERE, msg);
    }

    /**
     * Log info to the console.
     *
     * @param msg The message to log.
     */
    public static void logInfo(String msg) {
        instance.getLogger().info(msg);
    }
}
