package co.kepler.fastcraftplus.gui;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 * A GUI layout that encapsulates multiple sub-layouts.
 */
public class GUITabbedLayout extends GUILayout {
    private final GUILayout[] tabs;
    private GUILayout curTab;

    public GUITabbedLayout(GUI gui, GUILayout... tabs) {
        super(gui);
        assert tabs.length > 0;
        this.tabs = tabs;
        curTab = tabs[0];
    }

    /**
     * Set the current tab.
     * @param tab The tab page to set.
     */
    public void setTabIndex(int tab) {
        assert tab < tabs.length;
        curTab = tabs[tab];
    }

    /**
     * Get the current tab page.
     * @return Returns the current tab page.
     */
    public GUILayout getCurTab() {
        return curTab;
    }

    @Override
    public GUIButton getButton(int slot) {
        return curTab.getButton(slot);
    }

    @Override
    public GUIButton getButton(int row, int col) {
        return curTab.getButton(row, col);
    }

    @Override
    public GUIButton getPendingButton(int slot) {
        return curTab.getPendingButton(slot);
    }

    @Override
    public GUIButton getPendingButton(int row, int col) {
        return curTab.getPendingButton(row, col);
    }

    @Override
    public void setPendingButton(int slot, GUIButton button) {
        curTab.setPendingButton(slot, button);
    }

    @Override
    public void setPendingButton(int row, int col, GUIButton button) {
        curTab.setPendingButton(row, col, button);
    }

    @Override
    public void removePendingButton(int slot) {
        curTab.removePendingButton(slot);
    }

    @Override
    public void removePendingButton(int row, int col) {
        curTab.removePendingButton(row, col);
    }

    @Override
    public void updateGUI() {
        updateGUI(curTab);
    }

    public static class GUIButtonTab extends GUIButton {
        private final int tabPage;

        public GUIButtonTab(ItemStack item, int tabPage) {
            super(item);
            this.tabPage = tabPage;
        }

        @Override
        public boolean isVisible(GUILayout layout) {
            return true;
        }

        @Override
        public void onClick(GUILayout layout, InventoryClickEvent invEvent) {
            if (layout instanceof GUITabbedLayout) {
                GUITabbedLayout tabbedLayout = (GUITabbedLayout) layout;
                tabbedLayout.setTabIndex(tabPage);
            }
        }
    }
}
