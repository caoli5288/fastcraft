package co.kepler.fastcraftplus.craftgui;

import co.kepler.fastcraftplus.gui.Layout;
import co.kepler.fastcraftplus.gui.LayoutMulti;
import co.kepler.fastcraftplus.gui.LayoutPaged;
import org.bukkit.inventory.Recipe;

import java.util.HashSet;
import java.util.Set;

/**
 * The button layout for the FastCraft GUI.
 */
public class LayoutFastCraft extends LayoutMulti {
    private final GUIFastCraft gui;
    private final Set<Recipe> activeRecipes;

    private final Layout layoutNavbar;
    private final LayoutRecipesBasic layoutCraftingBasic;
    private final LayoutRecipesArmor layoutCraftingArmor;
    private final LayoutRecipesRepair layoutCraftingRepair;
    private final LayoutRecipesFireworks layoutCraftingFireworks;

    public LayoutFastCraft(GUIFastCraft gui) {
        super(new LayoutRecipesBasic(gui), new Layout(), 0);
        this.gui = gui;
        activeRecipes = new HashSet<>();

        // Initialize layout pages
        layoutNavbar = getBottomLayout();
        layoutCraftingBasic = (LayoutRecipesBasic) getTopLayout();
        layoutCraftingArmor = new LayoutRecipesArmor(gui);
        layoutCraftingRepair = new LayoutRecipesRepair(gui);
        layoutCraftingFireworks = new LayoutRecipesFireworks(gui);
    }

    @Override
    public void setHeight(int height) {
        super.setHeight(height);
        setTopLayoutHeight(height - 1);
    }

    /**
     * Get the crafting layout's navbar layout.
     *
     * @return Returns the navbar layout.
     */
    public Layout getLayoutNavbar() {
        return layoutNavbar;
    }

    /**
     * Get one of the layout tabs.
     *
     * @return Returns a layout tab.
     */
    public LayoutRecipes getLayoutTab(CraftingTab tab) {
        switch (tab) {
            case CRAFTING:
                return layoutCraftingBasic;
            case ARMOR:
                return layoutCraftingArmor;
            case REPAIR:
                return layoutCraftingRepair;
            case FIREWORKS:
                return layoutCraftingFireworks;
            default:
                return null;
        }
    }

    /**
     * Show a layout tab.
     *
     * @param tab The layout tab to show.
     */
    public void showLayout(CraftingTab tab) {
        LayoutRecipes layout = getLayoutTab(tab);
        setTopLayout(layout);
        layout.updateRecipes();
    }
}
