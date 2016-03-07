package co.kepler.fastcraftplus.gui;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 * A GUI layout that is organized into pages.
 */
public class GUIPagedLayout extends GUILayout {
	private NavPosition navPosition;
	private GUIButton[] navButtons = new GUIButton[9];
	private int page = 0;
	private int maxSlotIndex = 0;
	
	/**
	 * Create a new paged GUI layout.
	 * @param navPosition
	 */
	public GUIPagedLayout(GUI gui, NavPosition navPosition) {
		super(gui);
		this.navPosition = navPosition;
	}
	
	@Override
	public void setPendingButton(int slot, GUIButton button) {
		super.setPendingButton(slot, button);
		if (button == null) {
			maxSlotIndex = 0;
			for (Integer i : buttons.keySet()) {
				maxSlotIndex = Math.max(maxSlotIndex, i);
			}
		} else {
			maxSlotIndex = Math.max(maxSlotIndex, slot);
		}
	}
	
	/**
	 * Set a button in the navigation bar.
	 * @param col The column in the navigation bar to add the button to.
	 * @param button The button to add to the navigation bar.
	 */
	public void setNavButton(int col, GUIButton button) {
		assert 0 <= col && col < navButtons.length;
		navButtons[col] = button;
	}
	
	/**
	 * Remove a button from the navigation bar.
	 * @param col The column of the button to remove.
	 */
	public void removeNavButton(int col) {
		setNavButton(col, null);
	}
	
	/**
	 * Get the position of the navigation bar.
	 * @return Returns the position of the navigation bar.
	 */
	public NavPosition getNavPosition() {
		return navPosition;
	}
	
	/**
	 * Get the number of buttons shown on each page of the GUI.
	 * @param gui The GUI being checked.
	 * @return Returns the number of buttons shown on each page of the GUI.
	 */
	public int getButtonsPerPage(int guiRows) {
		switch (navPosition) {
		default:
		case NONE:
			return guiRows * 9;
		case TOP:
		case BOTTOM:
			return guiRows * 9 - 1;
		}
	}
	
	/**
	 * Get the number of buttons shown on each page of the GUI.
	 * @param gui The GUI being checked.
	 * @return Returns the number of buttons shown on each page of the GUI.
	 */
	public int getButtonsPerPage() {
		return getButtonsPerPage(gui.getRowCount());
	}
	
	/**
	 * Get the number of pages.
	 * @param gui The GUI being checked.
	 * @return Returns the number of pages.
	 */
	public int getPageCount() {
		return maxSlotIndex / getButtonsPerPage() + 1;
	}
	
	/**
	 * Get the current page.
	 * @return Returns the current page.
	 */
	public int getPage() {
		return page;
	}
	
	/**
	 * Set the current page.
	 * @param page Which page to go to.
	 */
	public void setPage(int page) {
		assert 0 <= page && page < getPageCount();
	}
	
	@Override
	public GUIButton getButton(int slot) {
		int[] rawSlotPos = getSlotPos(slot);
		int guiRowCount = gui.getRowCount();
		slot += getButtonsPerPage(guiRowCount) * page;
		
		switch (navPosition) {
		default:
		case NONE:
			return super.getButton(slot);
		case TOP:
			if (rawSlotPos[0] == 0) {
				// If the row is the top row
				return navButtons[rawSlotPos[1]];
			}
			return getButton(slot - 9);
		case BOTTOM:
			if (rawSlotPos[0] == guiRowCount - 1) {
				// If the row is the bottom row
				return navButtons[rawSlotPos[1]];
			}
			return getButton(slot);
		}
	}
	
	/**
	 * The position of the navigation buttons in the GUI.
	 */
	public static enum NavPosition {
		/** The navigation buttons will not be shown. */
		NONE,
		
		/** The navigation buttons will be shown at the top of the GUI. */
		TOP,
		
		/** The navigation buttons will be shown at the bottom of the GUI. */
		BOTTOM,
	}
	
	public class GUIButtonNextPage extends GUIButton {
		public GUIButtonNextPage(ItemStack item) {
			super(item);
		}

		@Override
		public boolean isVisible(GUILayout layout) {
			if (layout instanceof GUIPagedLayout) {
				GUIPagedLayout pagedLayout = (GUIPagedLayout) layout;
				return pagedLayout.getPage() < pagedLayout.getPageCount() - 1;
			}
			return true;
		}

		@Override
		public void onClick(GUILayout layout, InventoryClickEvent invEvent) {
			if (layout instanceof GUIPagedLayout) {
				GUIPagedLayout pagedLayout = (GUIPagedLayout) layout;
				pagedLayout.setPage(pagedLayout.getPage() + 1);
			}
		}
	}
}
