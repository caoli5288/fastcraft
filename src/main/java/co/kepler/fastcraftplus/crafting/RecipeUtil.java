package co.kepler.fastcraftplus.crafting;

import co.kepler.fastcraftplus.FastCraft;
import com.google.common.collect.Iterators;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Recipe;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Utility methods for recipes.
 */
public class RecipeUtil {
    private static final String[] IGNORE_RECIPES = new String[]{
            "RecipeArmorDye", "RecipeBookClone", "RecipeMapClone",
            "RecipeMapExtend", "RecipeFireworks", "RecipeRepair"
    };

    private static List<FastRecipe> recipes = new ArrayList<>();
    private static Object craftingManagerInstance;
    private static Method methodGetRecipes;
    private static Method methodToBukkitRecipe;
    private static List<Class> ignoreRecipeClasses;

    public static void init() {
        String version = Bukkit.getServer().getClass().getPackage().getName();
        version = version.substring(version.lastIndexOf('.') + 1);
        String nms = "net.minecraft.server." + version + ".";
        String cb = "org.bukkit.craftbukkit." + version + ".";

        ignoreRecipeClasses = new ArrayList<>();
        for (String s : IGNORE_RECIPES) {
            try {
                ignoreRecipeClasses.add(Class.forName(nms + s));
            } catch (ClassNotFoundException e) {
                FastCraft.logInfo("Class '" + s + "' does not exist");
            }
        }

        try {
            Class<?> classCraftingManager = Class.forName(nms + "CraftingManager");
            craftingManagerInstance = classCraftingManager.getMethod("getInstance").invoke(null);
            methodGetRecipes = craftingManagerInstance.getClass().getMethod("getRecipes");
            methodToBukkitRecipe = Class.forName(nms + "IRecipe").getMethod("toBukkitRecipe");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<FastRecipe> getRecipes() {
        if (recipes == null || Iterators.size(Bukkit.recipeIterator()) != recipes.size()) {
            // If recipes is uninitialized, or if the recipes have changed.
            loadRecipes();
        }
        return recipes;
    }

    private static boolean ignoreRecipe(Object iRecipe) {
        for (Class c : ignoreRecipeClasses) {
            if (c.isInstance(iRecipe)) {
                return true;
            }
        }
        return false;
    }

    private static void loadRecipes() {
        try {
            recipes.clear();
            for (Object iRecipe : (List) methodGetRecipes.invoke(craftingManagerInstance)) {
                if (ignoreRecipe(iRecipe)) continue;
                Recipe recipe = (Recipe) methodToBukkitRecipe.invoke(iRecipe);
                if (!FastRecipe.canBeFastRecipe(recipe)) continue;
                recipes.add(new FastRecipe(recipe));
            }
            Collections.sort(recipes);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
