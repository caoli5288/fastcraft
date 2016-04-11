package co.kepler.fastcraftplus.config;

import co.kepler.fastcraftplus.FastCraft;
import co.kepler.fastcraftplus.recipes.Ingredient;
import co.kepler.fastcraftplus.recipes.RecipeUtil;
import co.kepler.fastcraftplus.recipes.custom.CustomRecipe;
import co.kepler.fastcraftplus.recipes.custom.CustomShapedRecipe;
import co.kepler.fastcraftplus.recipes.custom.CustomShapelessRecipe;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import java.util.*;

/**
 * Manages recipes from the recipes config file.
 */
public class RecipesConfig extends ConfigExternal {
    private static final List<CustomRecipe> recipes = new ArrayList<>();

    public RecipesConfig() {
        super(true);
        setConfigs("recipes.yml");
    }

    @Override
    public void load() {
        super.load();

        // Remove loaded recipes
        for (CustomRecipe customRecipe : recipes) {
            for (Iterator<Recipe> iter = Bukkit.recipeIterator(); iter.hasNext(); ) {
                Recipe recipe = iter.next();
                if (RecipeUtil.areEqual(recipe, customRecipe.getRecipe())) {
                    iter.remove();
                    break;
                }
            }
        }

        // Load recipes
        recipes.clear();
        for (String key : config.getKeys(false)) {
            try {
                CustomRecipe customRecipe = getRecipe(config.getConfigurationSection(key));
                recipes.add(customRecipe);
                Bukkit.addRecipe(customRecipe.getRecipe());
                FastCraft.log("Loaded recipe: " + key);
            } catch (RecipeException e) {
                FastCraft.err("Error loading recipe '" + key + "': " + e.getMessage());
            }
        }
    }

    /**
     * Get a list of loaded recipes.
     *
     * @return The list of loaded recipes.
     */
    public List<CustomRecipe> getRecipes() {
        return recipes;
    }

    /**
     * Get a recipe from a configuration section.
     *
     * @param conf The configuration section containing the recipe.
     * @return Returns a recipe.
     * @throws RecipeException Thrown if the recipe is improperly configured.
     */
    private CustomRecipe getRecipe(ConfigurationSection conf) throws RecipeException {
        String type = conf.getString("type");
        if (type == null) {
            throw new RecipeException("Recipe type cannot be null");
        }
        switch (type.toLowerCase()) {
            case "shaped":
                return getShapedRecipe(conf);
            case "shapeless":
                return getShapelessRecipe(conf);
        }
        throw new RecipeException("Invalid recipe type for : '" + type + "'");
    }

    /**
     * Get a shaped recipe from a configuration section.
     *
     * @param conf The configuration section containing the recipe.
     * @return Returns a shaped recipe.
     * @throws RecipeException Thrown if the recipe is improperly configured.
     */
    private CustomRecipe getShapedRecipe(ConfigurationSection conf) throws RecipeException {
        // Create the recipe object
        ItemStack result = getItemStack(conf.getStringList("result"));

        // Add ingredients to map
        Map<Character, Ingredient> ingredients = new HashMap<>();
        ConfigurationSection ingredientSection = conf.getConfigurationSection("ingredients");
        for (String key : ingredientSection.getKeys(false)) {
            if (key.length() != 1) throw new RecipeException("Invalid ingredient character: '" + key + "'");
            ingredients.put(key.charAt(0), getIngredient(ingredientSection.getStringList(key)));
        }

        // Set recipe shape
        List<String> shape = conf.getStringList("shape");

        // return the new recipe
        return new CustomShapedRecipe(result, ingredients, shape);
    }

    /**
     * Get a shapeless recipe from a configuration section.
     *
     * @param conf The configuration section containing the recipe.
     * @return Returns a shapeless recipe.
     * @throws RecipeException Thrown if the recipe is improperly configured.
     */
    private CustomRecipe getShapelessRecipe(ConfigurationSection conf) throws RecipeException {
        // Create the recipe object
        ItemStack result = getItemStack(conf.getStringList("result"));

        // Add ingredients
        Map<Ingredient, Integer> ingredients = new HashMap<>();
        ConfigurationSection ingredientSection = conf.getConfigurationSection("ingredients");
        for (String key : ingredientSection.getKeys(false)) {
            ItemStack item = getItemStack(ingredientSection.getStringList(key));
            Ingredient ingredient = new Ingredient(item);
            Integer amount = ingredients.get(ingredient);
            ingredients.put(ingredient, (amount == null ? 0 : amount) + item.getAmount());
        }

        // Return the created recipe
        return new CustomShapelessRecipe(result, ingredients);
    }

    /**
     * Get an item
     *
     * @param item The List of Strings, without an amount, representing the Item.
     * @return Returns an ItemStack.
     * @throws RecipeException Thrown if the item is improperly configured.
     */
    @SuppressWarnings("deprecation")
    private Ingredient getIngredient(List<String> item) throws RecipeException {
        if (item.isEmpty() || item.size() > 3) throw new RecipeException("Item must have 1, 2, or 3 parameters");

        // Get the item's material
        String typeStr = item.get(0);
        Material type = Bukkit.getUnsafe().getMaterialFromInternalName(typeStr);
        if (type == Material.AIR) throw new RecipeException("Unknown material: '" + typeStr + "'");

        // Create the resulting item
        ItemStack result = new ItemStack(type);

        // Get the item's data
        byte data = 0;
        if (item.size() >= 2) {
            String dataStr = item.get(1);
            if (dataStr.equalsIgnoreCase("ANY")) {
                data = -1;
            } else {
                try {
                    data = Byte.parseByte(dataStr);
                } catch (NumberFormatException e) {
                    throw new RecipeException("Invalid item data: '" + dataStr + "'");
                }
            }
        }
        if (data != 0) {
            result.setData(type.getNewData(data));
        }

        // Get the item's metadata
        if (item.size() >= 3) {
            String metaStr = item.get(2);
            Bukkit.getUnsafe().modifyItemStack(result, metaStr);
        }

        // Return the ingredient
        return new Ingredient(result);
    }

    /**
     * Get an ItemStack with an amount from a List of Strings in the config.
     *
     * @param item The List of Strings, with an amount, representing the Item.
     * @return Returns an ItemStack.
     * @throws RecipeException Throws an exception if the item is improperly configured.
     */
    private ItemStack getItemStack(List<String> item) throws RecipeException {
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
        return getIngredient(item).toItemStack(amount);
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
