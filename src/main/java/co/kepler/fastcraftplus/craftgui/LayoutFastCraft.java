package co.kepler.fastcraftplus.craftgui;

import co.kepler.fastcraftplus.gui.LayoutPaged;
import co.kepler.fastcraftplus.gui.Layout;
import co.kepler.fastcraftplus.gui.LayoutMulti;
import org.bukkit.inventory.Recipe;

import java.util.HashSet;
import java.util.Set;

/**
 * The button layout for the FastCraft GUI.
 */
public class LayoutFastCraft extends LayoutMulti {
    private final Layout layoutNavbar;
    private final LayoutPaged layoutCrafting;
    private final LayoutPaged layoutArmor;
    private final LayoutPaged layoutRepair;
    private final LayoutPaged layoutFireworks;

    private final Set<Recipe> activeRecipes;

    public LayoutFastCraft() {
        super(new LayoutPaged(), new Layout(), 0);
        activeRecipes = new HashSet<>();

        // Initialize layout pages
        layoutNavbar = getBottomLayout();
        layoutCrafting = (LayoutPaged) getTopLayout();
        layoutArmor = new LayoutPaged();
        layoutRepair = new LayoutPaged();
        layoutFireworks = new LayoutPaged();
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
    public LayoutPaged getLayoutTab(CraftingTab tab) {
        switch (tab) {
            case CRAFTING:
                return layoutCrafting;
            case ARMOR:
                return layoutArmor;
            case REPAIR:
                return layoutRepair;
            case FIREWORKS:
                return layoutFireworks;
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
        setTopLayout(getLayoutTab(tab));
    }
}
