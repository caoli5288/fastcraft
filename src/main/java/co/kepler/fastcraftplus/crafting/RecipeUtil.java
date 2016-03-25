package co.kepler.fastcraftplus.crafting;

import co.kepler.fastcraftplus.FastCraft;
import com.google.common.collect.Iterators;
import org.bukkit.Achievement;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Utility methods for recipes.
 */
public class RecipeUtil {
    private static final String[] IGNORE_RECIPES = new String[]{
            "RecipeArmorDye", "RecipeBookClone", "RecipeMapClone", "RecipeMapExtend",
            "RecipeFireworks", "RecipeRepair", "RecipesBanner"
    };

    private static RecipeUtil instance;

    private List<FastRecipe> recipes = new ArrayList<>();
    private Object craftingManagerInstance;
    private Method methodGetRecipes;
    private Method methodToBukkitRecipe;
    private List<Class> ignoreRecipeClasses;
    private Method methodAsNMSCopy;
    private Method methodNMSGetName;

    private Map<Material, Achievement> craftingAchievements;

    /**
     * Create a new instance of RecipeUtil
     */
    private RecipeUtil() {
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

            Class<?> classCraftItemStack = Class.forName(cb + "inventory.CraftItemStack");
            Class<?> classItemStack = Class.forName(nms + "ItemStack");
            methodAsNMSCopy = classCraftItemStack.getMethod("asNMSCopy", ItemStack.class);
            methodNMSGetName = classItemStack.getMethod("getName");
        } catch (Exception e) {
            e.printStackTrace();
        }

        craftingAchievements = new HashMap<>();
        craftingAchievements.put(Material.WORKBENCH, Achievement.BUILD_WORKBENCH);
        craftingAchievements.put(Material.WOOD_PICKAXE, Achievement.BUILD_PICKAXE);
        craftingAchievements.put(Material.FURNACE, Achievement.BUILD_FURNACE);
        craftingAchievements.put(Material.WOOD_HOE, Achievement.BUILD_HOE);
        craftingAchievements.put(Material.BREAD, Achievement.MAKE_BREAD);
        craftingAchievements.put(Material.CAKE, Achievement.BAKE_CAKE);
        craftingAchievements.put(Material.STONE_PICKAXE, Achievement.BUILD_BETTER_PICKAXE);
        craftingAchievements.put(Material.WOOD_SWORD, Achievement.BUILD_SWORD);
    }

    /**
     * Get an instance of RecipeUtil.
     *
     * @return Returns an instance of RecipeUtil.
     */
    public static RecipeUtil getInstance() {
        if (instance == null) {
            instance = new RecipeUtil();
        }
        return instance;
    }

    public List<FastRecipe> getRecipes() {
        if (recipes == null || Iterators.size(Bukkit.recipeIterator()) != recipes.size()) {
            // If recipes is uninitialized, or if the recipes have changed.
            loadRecipes();
        }
        return recipes;
    }

    /**
     * See if a recipe should be ignored.
     *
     * @param iRecipe The recipe to check.
     * @return Returns true if the recipe should be ignored.
     */
    private boolean ignoreRecipe(Object iRecipe) {
        for (Class c : ignoreRecipeClasses) {
            if (c.isInstance(iRecipe)) {
                // If this class should be ignored
                return true;
            } else if (c.equals(iRecipe.getClass().getEnclosingClass())) {
                // If the enclosing class should be ignored
                return true;
            }
        }
        return false;
    }

    /**
     * Load the server's recipes as FastRecipe's
     */
    private void loadRecipes() {
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

    /**
     * Get the name of an item.
     *
     * @param item The item to get the name of.
     * @return Returns the name of the item.
     */
    public String getItemName(ItemStack item) {
        if (item == null) return "null";
        if (item.hasItemMeta()) {
            String displayName = item.getItemMeta().getDisplayName();
            if (displayName != null) return displayName;
        }

        // Try to get the item's name from lang
        String name = FastCraft.lang().items.name(item);
        if (name != null) return name;

        try {
            Object nmsItem = methodAsNMSCopy.invoke(null, item);
            name = (String) methodNMSGetName.invoke(nmsItem);
            if (name != null) return name;
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        new Exception("getItemName() can't find name").printStackTrace(); // TODO Remove
        return "ERROR";
    }

    /**
     * Award a player an achievement for crafting an item.
     *
     * @param player      The player to award the achievement to.
     * @param craftedItem The item the player crafted.
     */
    public void awardAchievement(Player player, ItemStack craftedItem) {
        Achievement a = craftingAchievements.get(craftedItem.getType());
        if (a == null) return;
        if (player.hasAchievement(a)) return;
        if (a.getParent() == null || player.hasAchievement(a.getParent())) {
            player.awardAchievement(a);
        }
    }
}
