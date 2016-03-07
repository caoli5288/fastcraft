package co.kepler.fastcraftplus.gui;

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
	public GUIPagedLayout(NavPosition navPosition) {
		super();
		this.navPosition = navPosition;
	}
	
	@Override
	public void setButton(int slot, GUIButton button) {
		super.setButton(slot, button);
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
	public int getButtonsPerPage(GUI gui) {
		return getButtonsPerPage(gui.getRowCount());
	}
	
	/**
	 * Get the number of pages.
	 * @param gui The GUI being checked.
	 * @return Returns the number of pages.
	 */
	public int getPageCount(GUI gui) {
		return maxSlotIndex / getButtonsPerPage(gui) + 1;
	}
	
	@Override
	public GUIButton getButton(int slot, GUI gui) {
		int[] rawSlotPos = getSlotPos(slot);
		int guiRowCount = gui.getRowCount();
		slot += getButtonsPerPage(guiRowCount) * page;
		
		switch (navPosition) {
		default:
		case NONE:
			return super.getButton(slot, gui);
		case TOP:
			if (rawSlotPos[0] == 0) {
				// If the row is the top row
				return navButtons[rawSlotPos[1]];
			}
			return getButton(slot - 9, gui);
		case BOTTOM:
			if (rawSlotPos[0] == guiRowCount - 1) {
				// If the row is the bottom row
				return navButtons[rawSlotPos[1]];
			}
			return getButton(slot, gui);
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
}
