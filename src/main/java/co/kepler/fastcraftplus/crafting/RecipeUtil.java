package co.kepler.fastcraftplus.crafting;

import co.kepler.fastcraftplus.FastCraft;
import com.google.common.collect.Iterators;
import org.bukkit.Achievement;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;

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

    private List<GUIRecipe> recipes = new ArrayList<>();
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

    /**
     * See if two recipes are equal.
     *
     * @param r0 A recipe to compare.
     * @param r1 A recipe to compare.
     * @return Returns true if the recipes are equal.
     */
    public static boolean areEqual(Recipe r0, Recipe r1) {
        if (r0 == r1) return true; // Are the same objects
        if (r0 == null ^ r1 == null) return false; // One is null, and one is not
        if (!r0.getResult().equals(r1.getResult())) return false; // Recipes have different results

        if (r0 instanceof ShapedRecipe) {
            if (!(r1 instanceof ShapedRecipe)) return false; // Not the same type of recipe
            ShapedRecipe sr0 = (ShapedRecipe) r0, sr1 = (ShapedRecipe) r1;

            return areShapesEqual(sr0, sr1); // Compare the shapes
        } else if (r0 instanceof ShapelessRecipe) {
            if (!(r1 instanceof ShapelessRecipe)) return false; // Not the same type of recipe
            ShapelessRecipe sr0 = (ShapelessRecipe) r0, sr1 = (ShapelessRecipe) r1;

            return sr0.getIngredientList().equals(sr1.getIngredientList()); // Compare ingredients
        } else if (r0 instanceof FurnaceRecipe) {
            if (!(r1 instanceof FurnaceRecipe)) return false; // Not the same type of recipe
            FurnaceRecipe fr0 = (FurnaceRecipe) r0, fr1 = (FurnaceRecipe) r1;

            return fr0.getInput().equals(fr1.getInput()); // Compare inputs
        }
        return false; // Unable to compare
    }

    /**
     * Compare the shape of two shaped recipes.
     *
     * @param sr0 A shaped recipe to compare.
     * @param sr1 A shaped recipe to compare.
     * @return Returns true if the recipes' shapes are the same.
     */
    public static boolean areShapesEqual(ShapedRecipe sr0, ShapedRecipe sr1) {
        String[] shape0 = sr0.getShape(), shape1 = sr1.getShape();
        if (shape0.length != shape1.length) return false; // Different number of rows

        int rows = shape0.length;
        ItemStack[][] items0 = new ItemStack[rows][], items1 = new ItemStack[rows][];
        for (int row = 0; row < shape0.length; row++) {
            char[] row0 = shape0[row].toCharArray(), row1 = shape1[row].toCharArray();
            if (row0.length != row1.length) return false; // Different number of columns
            int cols = row0.length;

            items0[row] = new ItemStack[cols];
            items1[row] = new ItemStack[cols];

            for (int col = 0; col < cols; col++) {
                items0[row][col] = sr0.getIngredientMap().get(row0[col]);
                items1[row][col] = sr1.getIngredientMap().get(row1[col]);
            }
        }

        for (int row = 0; row < items0.length; row++) {
            if (!Arrays.equals(items0[row], items1[row])) return false;
        }

        return true;
    }

    public List<GUIRecipe> getRecipes() {
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
     * Load the server's recipes as GUIRecipe's
     */
    private void loadRecipes() {
        try {
            recipes.clear();
            for (Object iRecipe : (List) methodGetRecipes.invoke(craftingManagerInstance)) {
                if (ignoreRecipe(iRecipe)) continue;
                Recipe recipe = (Recipe) methodToBukkitRecipe.invoke(iRecipe);
                if (!GUIRecipe.canBeGUIRecipe(recipe)) continue;
                recipes.add(new GUIRecipe(recipe));
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
