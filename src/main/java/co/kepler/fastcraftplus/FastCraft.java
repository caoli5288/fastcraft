package co.kepler.fastcraftplus;

import co.kepler.fastcraftplus.api.gui.GUI;
import co.kepler.fastcraftplus.commands.CommandManager;
import co.kepler.fastcraftplus.compat.RecipeCompatManager;
import co.kepler.fastcraftplus.config.ConfigExternal;
import co.kepler.fastcraftplus.config.LanguageConfig;
import co.kepler.fastcraftplus.config.PluginConfig;
import co.kepler.fastcraftplus.config.RecipesConfig;
import co.kepler.fastcraftplus.craftgui.GUIFastCraft;
import co.kepler.fastcraftplus.craftgui.PlayerManager;
import co.kepler.fastcraftplus.recipes.CraftingListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class FastCraft extends JavaPlugin {
    private static FastCraft instance;

    private List<ConfigExternal> externalConfigs;
    private PluginConfig config;
    private LanguageConfig lang;
    private RecipesConfig recipes;

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
        loadConfigs();

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
        GUI.disposeAll();
    }

    /**
     * Load all FastCraft+ configs.
     */
    public static void loadConfigs() {
        for (ConfigExternal conf : getInstance().externalConfigs) {
            conf.load();
        }
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
     * @return Return the plugin's configuration.
     */
    public static PluginConfig config() {
        return instance.config;
    }

    /**
     * Get the plugin's language configuration.
     *
     * @return Return the plugin's language configuration.
     */
    public static LanguageConfig lang() {
        return instance.lang;
    }

    /**
     * Get the plugin's recipes configuration.
     *
     * @return Return the plugin's recipes configuration.
     */
    public static RecipesConfig recipes() {
        return instance.recipes;
    }

    /**
     * Get the plugin's compatibility manager.
     *
     * @return Returns the plugin's compatibility manager.
     */
    public static RecipeCompatManager recipeManager() {
        return instance.recipeCompatManager;
    }

    public static PlayerManager playerManager() {
        return instance.playerManager;
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
