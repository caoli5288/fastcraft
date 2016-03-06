package co.kepler.fastcraftplus.gui;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;

public class GuiButtonClickEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	
	private final GuiButton button;
	private final InventoryClickEvent invEvent;
	
	/**
	 * Create a new InvButtonClickEvent.
	 * @param button The button that was clicked.
	 * @param click The type of click.
	 */
	public GuiButtonClickEvent(GuiButton button, Gui gui, InventoryClickEvent invEvent) {
		super();
		this.button = button;
		this.invEvent = invEvent;
	}

	/**
	 * Gets the button that was clicked.
	 * @return Returns the button that was clicked.
	 */
	public GuiButton getButton() {
		return button;
	}
	
	/**
	 * Gets the inventory event of the button click.
	 * @return Returns the inventory event of the button click.
	 */
	public InventoryClickEvent getInvEvent() {
		return invEvent;
	}

	/**
	 * Get a list of handlers.
	 * @return Returns a list of handlers.
	 */
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	/**
	 * Get a list of handlers.
	 * @return Returns a list of handlers.
	 */
	public static HandlerList getHandlerList() {
		return handlers;
	}
}
