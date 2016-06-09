package co.kepler.fastcraftplus.util;

import co.kepler.fastcraftplus.FastCraft;
import co.kepler.fastcraftplus.config.Config;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Utility class for Bukkit.
 */
public class BukkitUtil {
    private static boolean canGetNativeItemNames = false;
    private static Method methodAsNMSCopy, methodNMSGetName;

    private static Boolean supportsItemFlags = null;

    static {
        try {
            Class<?> classCraftItemStack = Class.forName(obc() + "inventory.CraftItemStack");
            Class<?> classItemStack = Class.forName(nms() + "ItemStack");
            methodAsNMSCopy = classCraftItemStack.getMethod("asNMSCopy", ItemStack.class);
            methodNMSGetName = classItemStack.getMethod("getName");
            canGetNativeItemNames = true;
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            FastCraft.err("Unable to load native Minecraft item names");
        }
    }

    /**
     * Get the server's version String.
     *
     * @return Return the server's version String.
     */
    public static String serverVersion() {
        String version = Bukkit.getServer().getClass().getPackage().getName();
        return version.substring(version.lastIndexOf('.') + 1);
    }

    /**
     * Get the net.minecraft.server classpath.
     *
     * @return Returns "net.minecraft.server.[version]."
     */
    public static String nms() {
        return "net.minecraft.server." + serverVersion() + ".";
    }

    /**
     * Get the org.bukkit.craftbukkit classpath.
     *
     * @return Returns "org.bukkit.craftbukkit.[version]."
     */
    public static String obc() {
        return "org.bukkit.craftbukkit." + serverVersion() + ".";
    }

    /**
     * Load into an existing YamlConfiguration from a Reader.
     *
     * @param stream The input stream to read the config from.
     * @param config The config to read into.
     */
    public static void loadConfiguration(InputStream stream, YamlConfiguration config) {
        try {
            Reader reader = new InputStreamReader(stream, Config.ENCODING);
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
     * @param file   The file to write to.
     */
    public static void saveConfiguration(YamlConfiguration config, File file) {
        try {
            OutputStream stream = new FileOutputStream(file);
            Writer writer = new OutputStreamWriter(stream, Config.ENCODING);
            writer.write(config.saveToString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the name of an item.
     *
     * @param item The item to get the name of.
     * @return Returns the name of the item.
     */
    public static String getItemName(ItemStack item) {
        if (item == null) return "null";

        // Return the item's display name if it has one.
        if (item.hasItemMeta()) {
            String displayName = item.getItemMeta().getDisplayName();
            if (displayName != null) return ChatColor.ITALIC + displayName;
        }

        // Try to get the item's name from lang
        String name = FastCraft.lang().items_name(item);
        if (name != null) return name;

        // Try to get the name from NMS
        if (canGetNativeItemNames) {
            try {
                Object nmsItem = methodAsNMSCopy.invoke(null, item);
                if (nmsItem != null) {
                    name = (String) methodNMSGetName.invoke(nmsItem);
                    if (name != null) return name;
                }
            } catch (IllegalAccessException | InvocationTargetException ignored) {
            }
        }

        // Return the item's name from its material type
        return WordUtils.capitalizeFully(item.getData().toString());
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
