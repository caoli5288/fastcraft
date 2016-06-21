package co.kepler.fastcraftplus.config;

import co.kepler.fastcraftplus.FastCraftPlus;
import co.kepler.fastcraftplus.recipes.Ingredient;
import co.kepler.fastcraftplus.recipes.custom.CustomRecipe;
import co.kepler.fastcraftplus.recipes.custom.CustomShapedRecipe;
import co.kepler.fastcraftplus.recipes.custom.CustomShapelessRecipe;
import co.kepler.fastcraftplus.util.RecipeUtil;
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
    private static final Map<RecipeUtil.ComparableRecipe, CustomRecipe> recipeMap = new HashMap<>();

    public RecipesConfig() {
        super(true, false);
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
        recipeMap.clear();
        for (String key : config.getKeys(false)) {
            try {
                CustomRecipe customRecipe = getConfigRecipe(config.getConfigurationSection(key));
                recipes.add(customRecipe);
                Bukkit.addRecipe(customRecipe.getRecipe());
                if (customRecipe.getRecipe() != null) {
                    recipeMap.put(RecipeUtil.comparable(customRecipe.getRecipe()), customRecipe);
                }
                FastCraftPlus.log("Loaded recipe: " + key);
            } catch (RecipeException e) {
                FastCraftPlus.err("Error loading recipe '" + key + "': " + e.getMessage());
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
     * Get a CustomRecipe given a Bukkit Recipe.
     *
     * @param recipe The Bukkit Recipe.
     * @return Returns a CustomRecipe.
     */
    public CustomRecipe getRecipe(Recipe recipe) {
        return recipeMap.get(RecipeUtil.comparable(recipe));
    }

    /**
     * Get a recipe from a configuration section.
     *
     * @param conf The configuration section containing the recipe.
     * @return Returns a recipe.
     * @throws RecipeException Thrown if the recipe is improperly configured.
     */
    private CustomRecipe getConfigRecipe(ConfigurationSection conf) throws RecipeException {
        String type = conf.getString("type");
        if (type == null) {
            throw new RecipeException("Recipe type cannot be null");
        }
        switch (type.toLowerCase()) {
        case "shaped":
            return getConfigShapedRecipe(conf);
        case "shapeless":
            return getConfigShapelessRecipe(conf);
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
    private CustomRecipe getConfigShapedRecipe(ConfigurationSection conf) throws RecipeException {
        // Create the recipe object
        ItemStack result = getIngredient(conf.getStringList("result"));

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
    private CustomRecipe getConfigShapelessRecipe(ConfigurationSection conf) throws RecipeException {
        // Create the recipe object
        ItemStack result = getIngredient(conf.getStringList("result"));

        // Add ingredients
        List<Ingredient> ingredients = new ArrayList<>();
        ConfigurationSection ingredientSection = conf.getConfigurationSection("ingredients");
        for (String key : ingredientSection.getKeys(false)) {
            ingredients.add(getIngredient(ingredientSection.getStringList(key)));
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
        if (item.size() < 2 || item.size() > 4) throw new RecipeException("Item must have 2, 3, or 4 parameters");

        // Get the item's amount
        String amountStr = item.get(0);
        int amount;
        try {
            amount = Integer.parseInt(item.get(0));
        } catch (NumberFormatException e) {
            throw new RecipeException("Invalid item amount: '" + amountStr + "'");
        }

        // Get the item's material
        String typeStr = item.get(1);
        Material type = Bukkit.getUnsafe().getMaterialFromInternalName(typeStr);
        if (type == Material.AIR) throw new RecipeException("Unknown material: '" + typeStr + "'");

        // Create the resulting item
        ItemStack result = new ItemStack(type, amount);

        // Get the item's data
        byte data = 0;
        if (item.size() >= 3) {
            String dataStr = item.get(2);
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
            result.setDurability(data);
        }

        // Get the item's metadata
        if (item.size() >= 4) {
            String metaStr = item.get(3);
            Bukkit.getUnsafe().modifyItemStack(result, metaStr);
        }

        // Return the ingredient
        return new Ingredient(result);
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
