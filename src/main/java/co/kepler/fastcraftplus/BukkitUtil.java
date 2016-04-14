package co.kepler.fastcraftplus;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Utility class for Bukkit.
 */
public class BukkitUtil {
    private static final String CONFIG_CHARSET = "UTF-8";

    private static String version = null;
    private static Boolean supportsItemFlags = null;

    /**
     * Get the server's version String.
     *
     * @return Return the server's version String.
     */
    public static String serverVersion() {
        if (version != null) return version;
        version = Bukkit.getServer().getClass().getPackage().getName();
        return version = version.substring(version.lastIndexOf('.') + 1);
    }

    /**
     * Load into an existing YamlConfiguration from a Reader.
     *
     * @param stream The input stream to read the config from.
     * @param config The config to read into.
     */
    public static void loadConfiguration(InputStream stream, YamlConfiguration config) {
        try {
            Reader reader = new InputStreamReader(stream, CONFIG_CHARSET);
            BufferedReader bufferedReader = new BufferedReader(reader);
            StringBuilder sb = new StringBuilder();

            // Read the stream into a String
            String curLine;
            while ((curLine = bufferedReader.readLine()) != null) {
                sb.append(curLine).append('\n');
            }

            config.loadFromString(sb.toString());
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Load a new YamlConfiguration from a Reader.
     *
     * @param stream The input stream to read the config from.
     * @return Returns a new YamlConfiguration.
     */
    public static YamlConfiguration loadConfiguration(InputStream stream) {
        YamlConfiguration result = new YamlConfiguration();
        loadConfiguration(stream, result);
        return result;
    }

    /**
     * Save a YamlConfiguration to a file.
     *
     * @param config The configuration to save.
     * @param file The file to write to.
     */
    public static void saveConfiguration(YamlConfiguration config, File file) {
        try {
            OutputStream stream = new FileOutputStream(file);
            Writer writer = new OutputStreamWriter(stream, CONFIG_CHARSET);
            writer.write(config.saveToString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * ItemFlag replacement for backwards compatibility with old bukkit versions.
     */
    public static class ItemFlag {
        public static Enum
                HIDE_ATTRIBUTES,
                HIDE_ENCHANTS,
                HIDE_DESTROYS,
                HIDE_PLACED_ON,
                HIDE_POTION_EFFECTS,
                HIDE_UNBREAKABLE;

        private static boolean supportsItemFlags;
        public static Class classItemFlag;
        public static Method addItemFlags;

        static {
            try {
                classItemFlag = Class.forName("org.bukkit.inventory.ItemFlag");
                addItemFlags = ItemMeta.class.getMethod("addItemFlags");

                HIDE_ATTRIBUTES = Enum.valueOf(classItemFlag, "HIDE_ATTRIBUTES");
                HIDE_ENCHANTS = Enum.valueOf(classItemFlag, "HIDE_ENCHANTS");
                HIDE_DESTROYS = Enum.valueOf(classItemFlag, "HIDE_DESTROYS");
                HIDE_PLACED_ON = Enum.valueOf(classItemFlag, "HIDE_PLACED_ON");
                HIDE_POTION_EFFECTS = Enum.valueOf(classItemFlag, "HIDE_POTION_EFFECTS");
                HIDE_UNBREAKABLE = Enum.valueOf(classItemFlag, "HIDE_UNBREAKABLE");

                supportsItemFlags = true;
            } catch (Exception e) {
                supportsItemFlags = false;
            }
        }

        /**
         * Add item flags to an ItemMeta, if possible.
         *
         * @param meta     The ItemMeta to add flags to.
         * @param itemFlag The flags to add.
         */
        public static void addItemFlags(ItemMeta meta, Enum... itemFlag) {
            if (!supportsItemFlags) return;
            try {
                addItemFlags.invoke(meta, (Object) itemFlag);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }
}
