package co.kepler.fastcraftplus;

import co.kepler.fastcraftplus.api.gui.GUI;
import co.kepler.fastcraftplus.commands.CommandManager;
import co.kepler.fastcraftplus.compat.RecipeCompatManager;
import co.kepler.fastcraftplus.config.*;
import co.kepler.fastcraftplus.craftgui.GUIFastCraft;
import co.kepler.fastcraftplus.craftgui.PlayerManager;
import co.kepler.fastcraftplus.recipes.CraftingListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

/**
 * The main class for FastCraft+
 */
public class FastCraft extends JavaPlugin {
    private static FastCraft instance;

    private List<ConfigExternal> externalConfigs;
    private PluginConfig config;
    private LanguageConfig lang;
    private RecipesConfig recipes;
    private BlacklistConfig blacklist;

    private RecipeCompatManager recipeCompatManager;
    private PlayerManager playerManager;

    @Override
    public void onEnable() {
        instance = this;

        // Create and load configurations
        externalConfigs = new ArrayList<>();
        externalConfigs.add(config = new PluginConfig());
        externalConfigs.add(lang = new LanguageConfig());
        externalConfigs.add(recipes = new RecipesConfig());
        externalConfigs.add(blacklist = new BlacklistConfig());
        load();

        // Load managers
        recipeCompatManager = new RecipeCompatManager();
        playerManager = new PlayerManager();

        // Register events
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new CraftingListener(), this);
        pluginManager.registerEvents(new GUIFastCraft.GUIListener(), this);
        pluginManager.registerEvents(playerManager, this);

        // Register commands
        new CommandManager().registerCommands();
    }

    @Override
    public void onDisable() {
        unload();
    }

    /**
     * Load FastCraft+.
     */
    public static void load() {
        for (ConfigExternal conf : getInstance().externalConfigs) {
            conf.load();
        }
    }

    /**
     * Unload FastCraft+.
     */
    public static void unload() {
        GUI.disposeAll();
        PlayerManager.Prefs.saveAllPrefs();
    }

    /**
     * Reload FastCraft+.
     */
    public void reload() {
        unload();
        load();
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
     * Get the plugin's configuration.
     *
     * @return Returns the configuration.
     */
    public static PluginConfig config() {
        return instance.config;
    }

    /**
     * Get the plugin's language configuration.
     *
     * @return Returns the language configuration.
     */
    public static LanguageConfig lang() {
        return instance.lang;
    }

    /**
     * Get the plugin's recipes configuration.
     *
     * @return Returns the configuration.
     */
    public static RecipesConfig recipes() {
        return instance.recipes;
    }

    /**
     * Get the plugin's blacklist configuration.
     *
     * @return Returns the blacklist configuration.
     */
    public static BlacklistConfig blacklist() {
        return instance.blacklist;
    }

    /**
     * Get the plugin's compatibility manager.
     *
     * @return Returns the compatibility manager.
     */
    public static RecipeCompatManager recipeManager() {
        return instance.recipeCompatManager;
    }

    /**
     * Get an instance of the player manager.
     *
     * @return Returns an instance of the player manager.
     */
    public static PlayerManager playerManager() {
        return instance.playerManager;
    }

    /**
     * Log an error message to the console.
     *
     * @param msg The message to log.
     */
    public static void err(String msg) {
        instance.getLogger().severe(ChatColor.RED + msg);
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
