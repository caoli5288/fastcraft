package co.kepler.fastcraftplus.config;

import co.kepler.fastcraftplus.FastCraft;
import co.kepler.fastcraftplus.recipes.FastRecipe;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Manages the blacklist configuration.
 */
public class BlacklistConfig extends ConfigExternal {
    private static final int HASH_RADIX = 24;
    private static final int HASH_LENGTH = 7;

    private static final Set<Integer> hashes = new HashSet<>();
    private List<BlacklistItem> items = new ArrayList<>();

    private boolean isBlacklist;

    public BlacklistConfig() {
        super(true, false);
        setConfigs("blacklist.yml");
    }

    @Override
    public void load() {
        super.load();

        // Get blacklist/whitelist type
        isBlacklist = !config.getBoolean("use-as-whitelist");

        // Load hashes
        hashes.clear();
        ConfigurationSection hashSection = config.getConfigurationSection("hashes");
        if (hashSection != null) {
            for (String key : hashSection.getKeys(false)) {
                try {
                    hashes.add(getHashInt(hashSection.getString(key)));
                } catch (NumberFormatException e) {
                    FastCraft.err("Invalid blacklist hashcode for " + key + ": " + e.getMessage());
                }
            }
        }

        // Load items
        items.clear();
        ConfigurationSection itemSection = config.getConfigurationSection("items");
        if (itemSection != null) {
            for (String key : itemSection.getKeys(false)) {
                try {
                    items.add(new BlacklistItem(itemSection.getStringList(key)));
                } catch (Exception e) {
                    FastCraft.err("Invalid blacklist item for " + key + ": " + e.getMessage());
                }
            }
        }
    }

    /**
     * Get a hashcode int from a configuration hashcode string.
     *
     * @param hash The hashcode string.
     * @return Returns a hashcode int.
     * @throws NumberFormatException Thrown when the hashcode string is invalid.
     */
    public static int getHashInt(String hash) throws NumberFormatException {
        return (int) Long.parseLong(hash, HASH_RADIX);
    }

    /**
     * Get a hashcode string from a hashcode int.
     *
     * @param hash The hashcode int to turn into a string.
     * @return Returns a hashcode string.
     */
    public static String getHashStr(int hash) {
        String hashStr = Long.toString(0xFFFFFFFFL & hash, HASH_RADIX);
        return StringUtils.leftPad(hashStr.toUpperCase(), HASH_LENGTH, '0');
    }

    /**
     * See if this recipe can be shown in the FastCraft+ interface.
     *
     * @param recipe The recipe to check.
     * @return Returns true if the recipe can be shown.
     */
    public boolean isAllowed(FastRecipe recipe) {
        boolean matched = false;
        if (!hashes.contains(recipe.hashCode())) {
            matched = true;
        } else {
            resultsLoop:
            for (ItemStack is : recipe.getResults()) {
                for (BlacklistItem item : items) {
                    if (item.matchesItem(is)) {
                        matched = true;
                        break resultsLoop;
                    }
                }
            }
        }
        return matched != isBlacklist;
    }

    /**
     * A blacklisted item.
     */
    private class BlacklistItem {
        private final Material type;
        private final Byte data;
        private final ItemMeta metadata;

        /**
         * Create a new blacklist item.
         *
         * @param item The item from the blacklist configuration.
         * @throws Exception Thrown when the item is invalid.
         */
        @SuppressWarnings("deprecation")
        private BlacklistItem(List<String> item) throws Exception {
            if (item.isEmpty()) throw new Exception("Item must have at least 1 parameter");
            if (item.size() > 3) throw new Exception("Item can have at most 3 parameters");

            // Get the item's type
            String typeStr = item.get(0);
            type = Bukkit.getUnsafe().getMaterialFromInternalName(typeStr);
            if (type == null) throw new Exception("Invalid item type (" + typeStr + ")");

            // Get the item's data
            String dataStr;
            if (item.size() > 1 && !"ANY".equalsIgnoreCase(dataStr = item.get(1))) {
                // If there is a data paramer, and it isn't ANY
                try {
                    data = Byte.parseByte(dataStr);
                } catch (NumberFormatException e) {
                    throw new Exception("Invalid item data (" + dataStr + ")");
                }
            } else {
                data = null;
            }

            // Get the item's metadata
            if (item.size() > 2) {
                String metadataStr = item.get(2);
                ItemStack is = new ItemStack(type);
                try {
                    Bukkit.getUnsafe().modifyItemStack(is, metadataStr);
                    metadata = is.getItemMeta();
                } catch (Exception e) {
                    throw new Exception("Invalid item metadata (" + metadataStr + ")");
                }
            } else {
                metadata = null;
            }
        }

        /**
         * Compare to an item to see if it matches.
         *
         * @param item The item to compare.
         * @return Returns true if the item matches.
         */
        @SuppressWarnings("deprecation")
        public boolean matchesItem(ItemStack item) {
            if (!item.getType().equals(type)) return false;
            if (data != null && item.getData().getData() != data) return false;
            return metadata == null || item.getItemMeta().equals(metadata);
        }
    }
}
