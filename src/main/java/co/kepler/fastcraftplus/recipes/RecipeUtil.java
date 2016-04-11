package co.kepler.fastcraftplus.recipes;

import co.kepler.fastcraftplus.BukkitUtil;
import co.kepler.fastcraftplus.FastCraft;
import org.bukkit.Achievement;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.*;
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

    private static Set<Integer> ignoreRecipeHashes;
    private static Method methodAsNMSCopy;
    private static Method methodNMSGetName;
    private static Method methodGetRecipes;
    private static Method methodToBukkitRecipe;
    private static Object craftingManagerInstance;

    private static Map<Material, Achievement> craftingAchievements;

    static {
        String version = BukkitUtil.serverVersion();
        String nms = "net.minecraft.server." + version + ".";
        String cb = "org.bukkit.craftbukkit." + version + ".";

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

        // Find which recipes should be ignored by FastCraft
        ignoreRecipeHashes = new HashSet<>();
        try {
            for (Object iRecipe : (List) methodGetRecipes.invoke(craftingManagerInstance)) {
                for (String ignoreType : IGNORE_RECIPES) {
                    Class recipeClass = iRecipe.getClass();
                    Class enclClass = iRecipe.getClass().getEnclosingClass();
                    if (recipeClass.getSimpleName().equals(ignoreType) ||
                            enclClass != null && enclClass.getSimpleName().equals(ignoreType)) {
                        Recipe recipe = (Recipe) methodToBukkitRecipe.invoke(iRecipe);
                        for (Iterator<Recipe> iter = Bukkit.recipeIterator(); iter.hasNext(); ) {
                            Recipe next = iter.next();
                            if (areEqual(recipe, next)) {
                                ignoreRecipeHashes.add(hashRecipe(next));
                            }
                        }
                    }
                }
            }
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
     * See if a recipe should be ignored.
     *
     * @param hash The recipe hash to check.
     * @return Returns true if the recipe should be ignored.
     */
    public static boolean shouldIgnoreRecipe(int hash) {
        return ignoreRecipeHashes.contains(hash);
    }

    /**
     * See if a recipe should be ignored.
     *
     * @param recipe The recipe to check.
     * @return Returns true if the recipe should be ignored.
     */
    public static boolean shouldIgnoreRecipe(Recipe recipe) {
        return shouldIgnoreRecipe(hashRecipe(recipe));
    }

    /**
     * Get the name of an item.
     *
     * @param item The item to get the name of.
     * @return Returns the name of the item.
     */
    public static String getItemName(ItemStack item) {
        if (item == null) return "null";

        // Return the item's display name if it has one.
        if (item.hasItemMeta()) {
            String displayName = item.getItemMeta().getDisplayName();
            if (displayName != null) return displayName;
        }

        // Try to get the item's name from lang
        String name = FastCraft.lang().items_name(item);
        if (name != null) return name;

        // Try to get the name from NMS
        try {
            Object nmsItem = methodAsNMSCopy.invoke(null, item);
            if (nmsItem != null) {
                name = (String) methodNMSGetName.invoke(nmsItem);
                if (name != null) return name;
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        // Return the item's name from its material type
        // FastCraft.err("Can't find item name: " + item); // TODO
        return item.getData().toString();
    }

    /**
     * Award a player an achievement for crafting an item.
     *
     * @param player      The player to award the achievement to.
     * @param craftedItem The item the player crafted.
     */
    public static void awardAchievement(Player player, ItemStack craftedItem) {
        Achievement a = craftingAchievements.get(craftedItem.getType());
        if (a == null) return;
        if (player.hasAchievement(a)) return;
        if (a.getParent() == null || player.hasAchievement(a.getParent())) {
            player.awardAchievement(a);
        }
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
     * Get a recipe's matrix of ingredients in the crafting table.
     *
     * @param recipe The recipe to get the matrix of.
     * @return Return a matrix, or null if the recipe can't fit in a crafting grid.
     */
    public static ItemStack[] getRecipeMatrix(Recipe recipe) {
        assert recipe instanceof ShapedRecipe || recipe instanceof ShapelessRecipe :
                "Recipe must be a ShapedRecipe or a ShapelessRecipe";

        ItemStack[] matrix = new ItemStack[9];
        if (recipe instanceof ShapedRecipe) {
            ShapedRecipe sr = (ShapedRecipe) recipe;
            Map<Character, ItemStack> ingredients = sr.getIngredientMap();
            String[] shape = sr.getShape();

            // Add items to the matrix
            if (shape.length > 3) return null;
            for (int row = 0; row < shape.length; row++) {
                String curRow = shape[row];
                if (curRow.length() > 3) return null;
                for (int col = 0; col < curRow.length(); col++) {
                    matrix[row * 3 + col] = ingredients.get(curRow.charAt(col));
                }
            }
        } else {
            ShapelessRecipe sr = (ShapelessRecipe) recipe;

            // Add items to the matrix
            int matIndex = 0;
            for (ItemStack item : sr.getIngredientList()) {
                ItemStack curStack = item.clone();
                curStack.setAmount(0);
                for (int i = 0; i < item.getAmount(); i++) {
                    if (matIndex >= matrix.length) return null;
                    matrix[matIndex++] = curStack;
                }
            }
        }
        return matrix;
    }

    /**
     * Call a PrepareItemCraftEvent for a player, with a matrix and a result item.
     *
     * @param player The player who will be opening the table.
     * @param matrix The matrix of items in the crafting grid.
     * @param result The item in the result slot of the crafting table.
     * @return Returns the called event.
     */
    public static PrepareItemCraftEvent callPrepareItemCraftEvent(Player player, Recipe recipe, ItemStack[] matrix,
                                                                  ItemStack result, Location location) {
        assert player != null : "Player must not be null";
        assert recipe != null : "Recipe must not be null";
        assert matrix != null : "Matrix must not be null";

        CraftingInvWrapper inv = new CraftingInvWrapper(player, location);
        inv.setRecipe(recipe);
        inv.setMatrix(matrix);
        inv.setResult(result);

        PrepareItemCraftEvent event = new PrepareItemCraftEvent(inv, inv.getView(player), false);
        Bukkit.getPluginManager().callEvent(event);

        return event;
    }

    /**
     * Call the CraftItemEvent to see if it's cancelled.
     *
     * @return Returns false if the event was cancelled.
     */
    public static boolean callCraftItemEvent(Player player, Recipe recipe, ItemStack[] matrix,
                                             ItemStack result, Location location) {
        assert player != null : "Player must not be null";
        assert recipe != null : "Recipe must not be null";
        assert matrix != null : "Matrix must not be null";

        CraftingInvWrapper inv = new CraftingInvWrapper(player, location);
        inv.setResult(recipe.getResult());

        CraftItemEvent event = new CraftItemEvent(recipe, inv.getView(player),
                InventoryType.SlotType.RESULT, 0, ClickType.UNKNOWN, InventoryAction.UNKNOWN);

        Bukkit.getPluginManager().callEvent(event);
        return !event.isCancelled() && event.getResult() != Event.Result.DENY;
    }

    public static int hashRecipe(Recipe r) {
        int hash = r.getResult().hashCode();
        if (r instanceof ShapedRecipe) {
            ShapedRecipe sr = (ShapedRecipe) r;
            for (String s : sr.getShape()) {
                for (char c : s.toCharArray()) {
                    hash *= 31;
                    ItemStack is = sr.getIngredientMap().get(c);
                    hash += is == null ? 0 : is.hashCode();
                }
            }
        } else if (r instanceof ShapelessRecipe) {
            hash *= 31;
            ShapelessRecipe sr = (ShapelessRecipe) r;
            for (ItemStack is : sr.getIngredientList()) {
                hash += is.hashCode();
            }
        }
        return hash;
    }
}
