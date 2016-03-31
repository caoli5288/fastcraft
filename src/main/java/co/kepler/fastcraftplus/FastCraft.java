package co.kepler.fastcraftplus;

import co.kepler.fastcraftplus.api.gui.GUI;
import co.kepler.fastcraftplus.compat.Compatibility;
import co.kepler.fastcraftplus.config.Config;
import co.kepler.fastcraftplus.config.Language;
import co.kepler.fastcraftplus.config.Recipes;
import co.kepler.fastcraftplus.craftgui.GUIFastCraft;
import co.kepler.fastcraftplus.crafting.CraftingListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class FastCraft extends JavaPlugin {
    private static FastCraft instance;

    private Config config;
    private Language lang;
    private Compatibility compat;

    @Override
    public void onEnable() {
        instance = this;

        config = new Config();
        lang = new Language(config.getLanguage());
        compat = new Compatibility();

        Recipes.loadRecipes();

        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new CraftingListener(), this);
        pluginManager.registerEvents(new GUIFastCraft.GUIListener(), this);
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
     * Get the plugin's compatibility manager.
     *
     * @return Returns the plugin's compatibility manager.
     */
    public static Compatibility compat() {
        return instance.compat;
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
        instance.getLogger().severe(msg);
    }

    /**
     * Log info to the console.
     *
     * @param msg The message to log.
     */
    public static void log(String msg) {
        instance.getLogger().info(msg);
    }

    /**
     * Log a warning message to the console.
     *
     * @param msg The message to log.
     */
    public static void warning(String msg) {
        instance.getLogger().warning(msg);
    }
}
