package co.kepler.fastcraftplus.config;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import java.util.List;

/**
 * Manages recipes from the recipes config file.
 */
public class Recipes {

    public static void loadRecipes() {

    }

    private static Recipe getRecipe(ConfigurationSection conf) {

    }

    private static Recipe getShapedRecipe(ConfigurationSection conf) {

    }

    private static Recipe getShapedRecipe(ConfigurationSection conf) {

    }

    private static Recipe getShapedRecipe(ConfigurationSection conf) {

    }

    private ItemStack getItem(List<String> item, int amount) throws Exception {
        if (item.isEmpty() || item.size() > 3) {
            throw new Exception("Item must have 1, 2, or 3 parameters");
        }

        // Get the item's material
        String typeStr = item.get(0);
        Material type = Bukkit.getUnsafe().getMaterialFromInternalName(typeStr);
        if (type == null) {
            throw new Exception("Unknown material: '" + typeStr + "'");
        }

        // Create the resulting item
        ItemStack result = new ItemStack(type, amount);

        // Get the item's data
        byte data = 0;
        if (item.size() >= 1) {
            String dataStr = item.get(1);
            if (dataStr.equalsIgnoreCase("ALL")) {
                data = -1;
            } else {
                try {
                    result.getData().setData(Byte.parseByte(dataStr));
                } catch (NumberFormatException e) {
                    throw new Exception("Invalid item data: '" + dataStr + "'");
                }
            }
        }
        result.getData().setData(data);

        // Get the item's metadata
        if (item.size() >= 2) {
            String metaStr = item.get(2);
            Bukkit.getUnsafe().modifyItemStack(result, metaStr);
        }

        // Return the item
        return result;
    }

    private ItemStack getItemWithAmount(List<?> item) throws Exception {
        if (item.size() != 2) {
            throw new Exception("Item with amount must only have two elements");
        }
        return getItem((List<String>) item.get(1), (Integer) item.get(0));
    }
}
