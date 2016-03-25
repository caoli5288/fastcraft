package co.kepler.fastcraftplus.craftgui;

import co.kepler.fastcraftplus.api.gui.Layout;
import co.kepler.fastcraftplus.api.gui.LayoutMulti;

/**
 * The button layout for the FastCraft GUI.
 */
public class LayoutFastCraft extends LayoutMulti {
    private static final int NAV_BUFFER = 1;

    private final GUIFastCraft gui;

    private final Layout layoutNavbar;
    private final LayoutRecipesItems layoutCraftingItems;
    private final LayoutRecipesArmor layoutCraftingArmor;
    private final LayoutRecipesFireworks layoutCraftingFireworks;

    public LayoutFastCraft(GUIFastCraft gui) {
        super(null, new Layout(), 0);
        this.gui = gui;

        // Initialize layout pages
        layoutNavbar = getBottomLayout();
        layoutCraftingItems = new LayoutRecipesItems(gui);
        layoutCraftingArmor = new LayoutRecipesArmor(gui);
        layoutCraftingFireworks = new LayoutRecipesFireworks(gui);

        // Show a layout
        showLayout(CraftingTab.CRAFTING);
    }

    @Override
    public void setHeight(int height) {
        super.setHeight(height);
        setTopLayoutHeight(height - 2);
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
                return layoutCraftingItems;
            case ARMOR:
                return layoutCraftingArmor;
            case FIREWORKS:
                return layoutCraftingFireworks;
            default:
                return null;
        }
    }

    /**
     * Get the current recipes layout.
     *
     * @return Returns the current recipes layout.
     */
    public LayoutRecipes getCurRecipesLayout() {
        return (LayoutRecipes) getTopLayout();
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
