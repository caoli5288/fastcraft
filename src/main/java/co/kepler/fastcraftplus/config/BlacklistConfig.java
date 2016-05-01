package co.kepler.fastcraftplus.config;

import co.kepler.fastcraftplus.FastCraft;
import co.kepler.fastcraftplus.Permissions;
import co.kepler.fastcraftplus.recipes.FastRecipe;
import co.kepler.fastcraftplus.recipes.Ingredient;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages the blacklist configuration.
 */
public class BlacklistConfig extends ConfigExternal {
    private static final String PERM = "fastcraft.blacklist.";
    private static final String PERM_HASH = PERM + "hash.";
    private static final String PERM_RESULT = PERM + "result.";
    private static final String PERM_INGREDIENT = PERM + "ingredient.";

    private static final int HASH_RADIX = 24;
    private static final int HASH_LENGTH = 7;

    private static final Map<Integer, List<Permission>> recipePerms = new HashMap<>(); // <hash, permissions>

    private static final Map<Integer, String> hashes = new HashMap<>(); // <hash, perm>
    private Map<BlacklistItem, String> results = new HashMap<>(); // <item, perm>
    private Map<BlacklistItem, String> ingredients = new HashMap<>(); // <item, perm>

    private boolean isBlacklist;

    public BlacklistConfig() {
        super(true, false);
        setConfigs("blacklist.yml");
    }

    @Override
    public void load() {
        super.load();

        // Unload permissions
        for (List<Permission> perms : recipePerms.values()) {
            for (Permission perm : perms) {
                Bukkit.getPluginManager().removePermission(perm);
            }
        }

        // Clear collections
        recipePerms.clear();
        hashes.clear();
        results.clear();
        ingredients.clear();

        // Get blacklist/whitelist type
        isBlacklist = !config.getBoolean("use-as-whitelist");

        // Load hashes
        ConfigurationSection hashSection = config.getConfigurationSection("hashes");
        if (hashSection == null) {
            FastCraft.err("Missing 'hashes' section in recipes.yml");
        } else {
            for (String key : hashSection.getKeys(false)) {
                try {
                    hashes.put(getHashInt(hashSection.getString(key)), PERM_HASH + key);
                } catch (NumberFormatException e) {
                    FastCraft.err("Invalid blacklist hashcode for " + key + ": " + e.getMessage());
                }
            }
        }

        // Load results
        ConfigurationSection resultSection = config.getConfigurationSection("results");
        if (resultSection == null) {
            FastCraft.err("Missing 'results' section in recipes.yml");
        } else {
            for (String key : resultSection.getKeys(false)) {
                try {
                    results.put(new BlacklistItem(resultSection.getStringList(key)), PERM_RESULT + key);
                } catch (Exception e) {
                    FastCraft.err("Invalid blacklist result for " + key + ": " + e.getMessage());
                }
            }
        }

        // Load ingredients
        ConfigurationSection ingredientSection = config.getConfigurationSection("ingredients");
        if (ingredientSection == null) {
            FastCraft.err("Missing 'ingredients' section in recipes.yml");
        } else {
            for (String key : ingredientSection.getKeys(false)) {
                try {
                    ingredients.put(new BlacklistItem(ingredientSection.getStringList(key)), PERM_INGREDIENT + key);
                } catch (Exception e) {
                    FastCraft.err("Invalid blacklist ingredient for " + key + ": " + e.getMessage());
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
        String hashStr = Long.toString(0xFFFFFFFFL & hash, HASH_RADIX); // Unsigned int to String
        return StringUtils.leftPad(hashStr.toUpperCase(), HASH_LENGTH, '0');
    }

    /**
     * See if this recipe can be shown in the FastCraft+ interface.
     *
     * @param recipe The recipe to check.
     * @param player The player using the recipe.
     * @return Returns true if the recipe can be shown.
     */
    public boolean isAllowed(FastRecipe recipe, Player player) {
        int hash = recipe.hashCode();
        if (!recipePerms.containsKey(hash)) {
            // Collect permissions required for this recipe to be shown
            List<String> permStrings = new ArrayList<>();

            // Get blacklist hash permissions
            if (hashes.containsKey(hash)) {
                permStrings.add(hashes.get(hash));
            }

            // Get blacklist result permissions
            for (ItemStack is : recipe.getResults()) {
                for (BlacklistItem bli : results.keySet()) {
                    if (!bli.matchesItem(is)) continue;
                    permStrings.add(results.get(bli));
                }
            }

            // Get blacklist ingredient permissions
            for (Ingredient ing : recipe.getIngredients().keySet()) {
                for (BlacklistItem bli : ingredients.keySet()) {
                    if (!bli.matchesItem(ing.toItemStack(1))) continue;
                    permStrings.add(ingredients.get(bli));
                }
            }

            // Get permissions, or register them if they don't exist
            List<Permission> perms = new ArrayList<>();
            for (String permString : permStrings) {
                Permission perm = Bukkit.getPluginManager().getPermission(permString);
                if (perm == null) {
                    perm = new Permission(permString, PermissionDefault.FALSE);
                    Bukkit.getPluginManager().addPermission(perm);
                    perm.addParent(Permissions.BLACKLIST_ALL, true);
                }
                perms.add(perm);
            }
            recipePerms.put(hash, perms);
        }

        // See if the player has all the required permissions
        boolean hasPerms = true;
        for (Permission perm : recipePerms.get(hash)) {
            if (!player.hasPermission(perm)) {
                hasPerms = false;
                break;
            }
        }

        // Return whether the recipe should be shown to the player
        if (isBlacklist) {
            return hasPerms;
        } else {
            return !hasPerms;
        }
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
            if (!type.equals(item.getType())) return false;
            if (data != null && item.getData().getData() != data) return false;
            return metadata == null || metadata.equals(item.getItemMeta());
        }
    }
}
