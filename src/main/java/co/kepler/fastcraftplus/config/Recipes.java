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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Manages recipes from the recipes config file.
 */
public class Recipes {
    private static final Set<Recipe> loadedRecipes = new HashSet<>();

    /**
     * Unloads all previously loaded recipes, then load recipes from the recipes config.
     * If the config does not exist, copy the resource to the plugin directory.
     */
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
            } catch (RecipeException e) {
                FastCraft.err("Error loading recipe '" + key + "': " + e.getMessage());
            }
        }
    }

    /**
     * Get a recipe from a configuration section.
     *
     * @param conf The configuration section containing the recipe.
     * @return Returns a furnace recipe.
     * @throws RecipeException Thrown if the recipe is improperly configured.
     */
    private static Recipe getRecipe(ConfigurationSection conf) throws RecipeException {
        String type = conf.getString("type");
        if (type == null) {
            throw new RecipeException("Recipe type cannot be null");
        }
        switch (type.toLowerCase()) {
            case "shaped":
                return getShapedRecipe(conf);
            case "shapeless":
                return getShapelessRecipe(conf);
            case "furnace":
                return getFurnaceRecipe(conf);
        }
        throw new RecipeException("Invalid recipe type for : '" + type + "'");
    }

    /**
     * Get a shaped recipe from a configuration section.
     *
     * @param conf The configuration section containing the recipe.
     * @return Returns a furnace recipe.
     * @throws RecipeException Thrown if the recipe is improperly configured.
     */
    private static Recipe getShapedRecipe(ConfigurationSection conf) throws RecipeException {
        // Create the recipe object
        ItemStack result = getAmountItem(conf.getStringList("result"));
        CustomShapedRecipe recipe = new CustomShapedRecipe(result);

        // Add ingredients to map
        ConfigurationSection ingredients = conf.getConfigurationSection("ingredients");
        for (String key : ingredients.getKeys(false)) {
            if (key.length() != 1) {
                throw new RecipeException("Invalid ingredient character: '" + key + "'");
            }
            recipe.setIngredient(key.charAt(0), getItem(ingredients.getStringList(key), 1));
        }

        // Set recipe shape
        List<String> shapeList = ingredients.getStringList("recipe");
        recipe.shape(shapeList.toArray(new String[shapeList.size()]));

        // return the new recipe
        return recipe;
    }

    /**
     * Get a shapeless recipe from a configuration section.
     *
     * @param conf The configuration section containing the recipe.
     * @return Returns a furnace recipe.
     * @throws RecipeException Thrown if the recipe is improperly configured.
     */
    private static Recipe getShapelessRecipe(ConfigurationSection conf) throws RecipeException {
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

    /**
     * Get a furnace recipe from a configuration section.
     *
     * @param conf The configuration section containing the recipe.
     * @return Returns a furnace recipe.
     * @throws RecipeException Thrown if the recipe is improperly configured.
     */
    private static Recipe getFurnaceRecipe(ConfigurationSection conf) throws RecipeException {
        // Create the recipe object
        ItemStack input = getItem(conf.getStringList("input"), 1);
        ItemStack result = getAmountItem(conf.getStringList("result"));
        float exp = (float) conf.getDouble("exp");

        // Return a new furnace recipe
        return new CustomFurnaceRecipe(input, result, exp);
    }

    /**
     * Get an item
     *
     * @param item   The List of Strings, without an amount, representing the Item.
     * @param amount The amount of items in the ItemStack.
     * @return Returns an ItemStack.
     * @throws RecipeException Thrown if the item is improperly configured.
     */
    @SuppressWarnings("deprecation")
    private static ItemStack getItem(List<String> item, int amount) throws RecipeException {
        if (item.isEmpty() || item.size() > 3) {
            throw new RecipeException("Item must have 1, 2, or 3 parameters");
        }

        // Get the item's material
        String typeStr = item.get(0);
        Material type = Bukkit.getUnsafe().getMaterialFromInternalName(typeStr);
        if (type == null) {
            throw new RecipeException("Unknown material: '" + typeStr + "'");
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
                    throw new RecipeException("Invalid item data: '" + dataStr + "'");
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

    /**
     * Get an ItemStack with an amount from a List of Strings in the config.
     *
     * @param item The List of Strings, with an amount, representing the Item.
     * @return Returns an ItemStack.
     * @throws RecipeException Throws an exception if the item is improperly configured.
     */
    private static ItemStack getAmountItem(List<String> item) throws RecipeException {
        if (item.size() < 2) {
            throw new RecipeException("Item with amount have at least two elements");
        }

        // Parse the item amount
        String amountStr = item.get(0);
        int amount;
        try {
            amount = Integer.parseInt(item.get(0));
        } catch (NumberFormatException e) {
            throw new RecipeException("Invalid item amount: '" + amountStr + "'");
        }

        // Create the item, and return it
        item.remove(0);
        return getItem(item, amount);
    }

    /**
     * Used when loading recipes from the config. If a recipe is improperly configured,
     * this exception will be thrown.
     */
    public static class RecipeException extends Exception {
        public RecipeException(String msg) {
            super(msg);
        }
    }
}
