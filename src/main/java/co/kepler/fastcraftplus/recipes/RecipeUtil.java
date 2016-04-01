package co.kepler.fastcraftplus.recipes;

import co.kepler.fastcraftplus.FastCraft;
import org.bukkit.Achievement;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility methods for recipes.
 */
public class RecipeUtil {
    private static RecipeUtil instance;

    private Method methodAsNMSCopy;
    private Method methodNMSGetName;

    private Map<Material, Achievement> craftingAchievements;

    /**
     * Create a new instance of RecipeUtil
     */
    private RecipeUtil() {
        try {
            String version = Bukkit.getServer().getClass().getPackage().getName();
            version = version.substring(version.lastIndexOf('.') + 1);
            String nms = "net.minecraft.server." + version + ".";
            String cb = "org.bukkit.craftbukkit." + version + ".";

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

        // Try to get the name from NMS
        try {
            Object nmsItem = methodAsNMSCopy.invoke(null, item);
            name = (String) methodNMSGetName.invoke(nmsItem);
            if (name != null) return name;
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        // Return the item's name from its material type
        new Exception("Can't find name of ItemStack: " + item).printStackTrace();
        return item.getData().toString();
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

    /**
     * Get the resulting item from crafting this in a crafting table.
     *
     * @param recipe The recipe to check.
     * @return Returns the item from the crafting table.
     */
    public ItemStack getCraftingResult(ShapedRecipe recipe, Player player) {
        Map<Character, ItemStack> ingredients = recipe.getIngredientMap();
        String[] shape = recipe.getShape();
        ItemStack[] matrix = new ItemStack[9];

        // Add items to the matrix
        if (shape.length > 3) return null;
        for (int row = 0; row < shape.length; row++) {
            String curRow = shape[row];
            if (curRow.length() > 3) return null;
            for (int col = 0; col < curRow.length(); col++) {
                matrix[row * 3 + col] = ingredients.get(curRow.charAt(col));
            }
        }

        // Return the item in the result slot of the inventory
        return callPrepareItemCraftEvent(player, matrix, recipe.getResult()).getInventory().getResult();
    }

    /**
     * See if a recipe is consistent. A recipe is consistent if its result is the same as
     * the resulting item when crafting in a crafting table.
     *
     * @param recipe The recipe to check.
     * @return Returns true if the recipe is consistent.
     */
    public ItemStack getCraftingResult(ShapelessRecipe recipe, Player player) {
        ItemStack[] matrix = new ItemStack[9];
        int matIndex = 0;

        // Add items to the matrix
        for (ItemStack item : recipe.getIngredientList()) {
            ItemStack curStack = item.clone();
            curStack.setAmount(0);
            for (int i = 0; i < item.getAmount(); i++) {
                if (matIndex >= matrix.length) return null;
                matrix[matIndex++] = curStack;
            }
        }

        // Return the item in the result slot of the inventory
        return callPrepareItemCraftEvent(player, matrix, recipe.getResult()).getInventory().getResult();
    }

    /**
     * Call a PrepareItemCraftEvent for a player, with a matrix and a result item.
     *
     * @param player The player who will be opening the table.
     * @param matrix The matrix of items in the crafting grid.
     * @param result The item in the result slot of the crafting table.
     * @return Returns the called event.
     */
    public PrepareItemCraftEvent callPrepareItemCraftEvent(Player player, ItemStack[] matrix, ItemStack result) {
        CraftingInvWrapper inv = new CraftingInvWrapper(player);
        inv.setMatrix(matrix);
        inv.setResult(result);

        PrepareItemCraftEvent event = new PrepareItemCraftEvent(inv, inv.getView(player), false);
        Bukkit.getPluginManager().callEvent(event);

        return event;
    }
}
