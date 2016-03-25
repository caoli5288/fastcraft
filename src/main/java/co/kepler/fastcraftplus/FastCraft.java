package co.kepler.fastcraftplus;

import co.kepler.fastcraftplus.api.gui.GUI;
import co.kepler.fastcraftplus.config.Language;
import co.kepler.fastcraftplus.craftgui.GUIFastCraft;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class FastCraft extends JavaPlugin implements Listener {
    private static FastCraft instance;

    private Language lang;

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
     * Log a message to the console at a certain level.
     *
     * @param level The level at which the message should be logged.
     * @param msg   The message to log.
     */
    public static void log(Level level, String msg) {
        instance.getLogger().log(level, msg);
    }

    /**
     * Log info to the console.
     *
     * @param msg The message to log.
     */
    public static void logInfo(String msg) {
        instance.getLogger().info(msg);
    }

    @Override
    public void onEnable() {
        instance = this;

        lang = new Language("EN"); // TODO Language from config

        GUIFastCraft.init();
    }

    @Override
    public void onDisable() {
        GUI.disposeAll();
    }

}
