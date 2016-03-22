package co.kepler.fastcraftplus.craftgui;

import co.kepler.fastcraftplus.gui.GUILayout;
import co.kepler.fastcraftplus.gui.GUILayoutMulti;
import co.kepler.fastcraftplus.gui.GUILayoutPaged;
import org.bukkit.inventory.Recipe;

import java.util.HashSet;
import java.util.Set;

/**
 * The button layout for the FastCraft GUI.
 */
public class GUILayoutCrafting extends GUILayoutMulti {
    private final GUILayout layoutNavbar;
    private final GUILayoutPaged layoutCrafting;
    private final GUILayoutPaged layoutArmor;
    private final GUILayoutPaged layoutRepair;
    private final GUILayoutPaged layoutFireworks;

    private final Set<Recipe> activeRecipes;

    public GUILayoutCrafting() {
        super(new GUILayoutPaged(), new GUILayout(), 0);
        activeRecipes = new HashSet<>();

        // Initialize layout pages
        layoutNavbar = getBottomLayout();
        layoutCrafting = (GUILayoutPaged) getTopLayout();
        layoutArmor = new GUILayoutPaged();
        layoutRepair = new GUILayoutPaged();
        layoutFireworks = new GUILayoutPaged();
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
    public GUILayout getLayoutNavbar() {
        return layoutNavbar;
    }

    /**
     * Get one of the layout tabs.
     *
     * @return Returns a layout tab.
     */
    public GUILayoutPaged getLayoutTab(CraftingTab tab) {
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
