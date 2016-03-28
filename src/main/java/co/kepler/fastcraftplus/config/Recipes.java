package co.kepler.fastcraftplus.config;

import co.kepler.fastcraftplus.FastCraft;
import co.kepler.fastcraftplus.recipes.CustomFurnaceRecipe;
import co.kepler.fastcraftplus.recipes.CustomShapedRecipe;
import co.kepler.fastcraftplus.recipes.CustomShapelessRecipe;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import java.io.File;
import java.util.*;
import java.util.logging.Level;

/**
 * Manages recipes from the recipes config file.
 */
public class Recipes {
    private static final Set<Recipe> loadedRecipes = new HashSet<>();

    public static void loadRecipes() {
        // Setup and load recipes config
        FastCraft fastcraft = FastCraft.getInstance();
        fastcraft.saveResource("recipes.yml", false);
        File recipesFile = new File(fastcraft.getDataFolder(), "recipes.yml");
        YamlConfiguration recipes = YamlConfiguration.loadConfiguration(recipesFile);

        // Remove already loaded recipes
        for (Iterator<Recipe> iter = Bukkit.recipeIterator(); iter.hasNext(); ) {
            if (loadedRecipes.contains(iter.next())) {
                iter.remove();
            }
        }

        // Load recipes
        loadedRecipes.clear();
        for (String key : recipes.getKeys(false)) {
            try {
                Recipe newRecipe = getRecipe(recipes.getConfigurationSection(key));
                Bukkit.addRecipe(newRecipe);
                loadedRecipes.add(newRecipe);
            } catch (Exception e) {
                FastCraft.err("Error loading recipe '" + key + "': " + e.getMessage());
            }
        }
    }

    private static Recipe getRecipe(ConfigurationSection conf) throws Exception {
        String type = conf.getString("type");
        if (type == null) {
            throw new Exception("Recipe type cannot be null");
        }
        switch (type.toLowerCase()) {
            case "shaped":
                return getShapedRecipe(conf);
            case "shapeless":
                return getShapelessRecipe(conf);
            case "furnace":
                return getFurnaceRecipe(conf);
        }
        throw new Exception("Invalid recipe type for : '" + type + "'");
    }

    private static Recipe getShapedRecipe(ConfigurationSection conf) throws Exception {
        // Create the recipe object
        ItemStack result = getAmountItem(conf.getStringList("result"));
        CustomShapedRecipe recipe = new CustomShapedRecipe(result);

        // Add ingredients to map
        ConfigurationSection ingredients = conf.getConfigurationSection("ingredients");
        for (String key : ingredients.getKeys(false)) {
            if (key.length() != 1) {
                throw new Exception("Invalid ingredient character: '" + key + "'");
            }
            recipe.setIngredient(key.charAt(0), getItem(ingredients.getStringList(key), 1));
        }

        // Set recipe shape
        List<String> shapeList = ingredients.getStringList("recipe");
        recipe.shape(shapeList.toArray(new String[shapeList.size()]));

        // return the new recipe
        return recipe;
    }

    private static Recipe getShapelessRecipe(ConfigurationSection conf) throws Exception {
        // Create the recipe object
        ItemStack result = getAmountItem(conf.getStringList("result"));
        CustomShapelessRecipe recipe = new CustomShapelessRecipe(result);

        // Add ingredients
        ConfigurationSection ingredients = conf.getConfigurationSection("ingredients");
        for (String key : ingredients.getKeys(false)) {
            recipe.addIngredient(getAmountItem(ingredients.getStringList(key)));
        }

        // Return the created recipe
        return recipe;
    }

    private static Recipe getFurnaceRecipe(ConfigurationSection conf) throws Exception {
        // Create the recipe object
        ItemStack input = getItem(conf.getStringList("input"), 1);
        ItemStack result = getAmountItem(conf.getStringList("result"));
        float exp = (float) conf.getDouble("exp");

        // Return a new furnace recipe
        return new CustomFurnaceRecipe(input, result, exp);
    }

    private static ItemStack getItem(List<String> item, int amount) throws Exception {
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

    private static ItemStack getAmountItem(List<String> item) throws Exception {
        if (item.size() < 2) {
            throw new Exception("Item with amount have at least two elements");
        }

        // Parse the item amount
        String amountStr = item.get(0);
        int amount;
        try {
            amount = Integer.parseInt(item.get(0));
        } catch (NumberFormatException e) {
            throw new Exception("Invalid item amount: '" + amountStr + "'");
        }

        // Create the item, and return it
        item.remove(0);
        return getItem(item, amount);
    }
}
