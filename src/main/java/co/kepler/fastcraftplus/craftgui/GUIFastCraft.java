package co.kepler.fastcraftplus.craftgui;

import co.kepler.fastcraftplus.FastCraft;
import co.kepler.fastcraftplus.gui.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * The FastCraft crafting GUI.
 */
public class GUIFastCraft extends GUI {
    private static final ChatColor BUTTON_NAME_COLOR = ChatColor.GREEN;

    private static Map<UUID, GUIFastCraft> guis;
    private static boolean listenersRegistered = false;

    private final LayoutFastCraft craftLayout;
    private final Player player;
    private final Location location;

    private final GUIButton btnPagePrev;
    private final GUIButton btnPageNext;
    private final GUIButton btnMultiCraft;
    private final GUIButton btnWorkbench;
    private final GUIButtonGlowing btnTabCrafting;
    private final GUIButtonGlowing btnTabArmor;
    private final GUIButtonGlowing btnTabRepair;
    private final GUIButtonGlowing btnTabFireworks;


    /**
     * Create a new instance of a FastCraft GUI.
     *
     * @param player The player who will be shown this GUI.
     */
    public GUIFastCraft(Player player, Location location) {
        super("FastCraft+", 6); // TODO Localize

        this.player = player;
        this.location = location;

        craftLayout = new LayoutFastCraft(this);
        setLayout(craftLayout);

        // Create buttons TODO Localize
        Layout navbar = craftLayout.getLayoutNavbar();

        btnPagePrev = new GUIButton(new GUIItemBuilder(Material.ARROW)
                .setDisplayName(BUTTON_NAME_COLOR + "Previous Page")
                .setHideInfo(true).build());
        btnPagePrev.setClickAction(info -> btnPagePrevClick(info));
        navbar.setButton(1, 0, btnPagePrev);

        btnPageNext = new GUIButton(new GUIItemBuilder(Material.ARROW)
                .setDisplayName(BUTTON_NAME_COLOR + "Next Page")
                .setHideInfo(true).build());
        btnPageNext.setClickAction(info -> btnPageNextClick(info));
        navbar.setButton(1, 8, btnPageNext);

        btnMultiCraft = new GUIButton(new GUIItemBuilder(Material.ANVIL)
                .setDisplayName(BUTTON_NAME_COLOR + "Craft Amount").build());
        btnMultiCraft.setClickAction(info -> btnMultiCraftClick(info));
        navbar.setButton(1, 6, btnMultiCraft);

        btnWorkbench = new GUIButton(new GUIItemBuilder(Material.WORKBENCH)
                .setDisplayName(BUTTON_NAME_COLOR + "Open Crafting Grid").build());
        btnWorkbench.setClickAction(info -> btnWorkbenchClick(info));
        navbar.setButton(1, 7, btnWorkbench);

        btnTabCrafting = new GUIButtonGlowing(new GUIItemBuilder(Material.STICK)
                .setDisplayName(BUTTON_NAME_COLOR + "Crafting")
                .setHideInfo(true).build());
        btnTabCrafting.setClickAction(info -> btnTabCraftingClick(info));
        btnTabCrafting.setGlowing(true);
        navbar.setButton(1, 1, btnTabCrafting);

        btnTabArmor = new GUIButtonGlowing(new GUIItemBuilder(Material.LEATHER_CHESTPLATE) // TODO Color chestplate
                .setDisplayName(BUTTON_NAME_COLOR + "Dyed Armor")
                .setHideInfo(true).build());
        btnTabArmor.setClickAction(info -> btnTabArmorClick(info));
        navbar.setButton(1, 2, btnTabArmor);

        btnTabRepair = new GUIButtonGlowing(new GUIItemBuilder(Material.IRON_PICKAXE)
                .setDisplayName(BUTTON_NAME_COLOR + "Repair Items")
                .setHideInfo(true).build());
        btnTabRepair.setClickAction(info -> btnTabRepairClick(info));
        navbar.setButton(1, 3, btnTabRepair);

        btnTabFireworks = new GUIButtonGlowing(new GUIItemBuilder(Material.FIREWORK)
                .setDisplayName(BUTTON_NAME_COLOR + "Fireworks")
                .setHideInfo(true).build());
        btnTabFireworks.setClickAction(info -> btnTabFireworksClick(info));
        navbar.setButton(1, 4, btnTabFireworks);

        // Update the GUI's layout
        updateLayout();

        // Register listeners if necessary
        if (!listenersRegistered) {
            Bukkit.getPluginManager().registerEvents(new GUIListener(), FastCraft.getInstance());
            guis = new HashMap<>();
        }
        guis.put(player.getUniqueId(), this);
    }

    @Override
    public void show(Player... players) {
        assert players.length == 1 && players[0].equals(player) :
                "FastCraft GUI can only be shown to its associated player";
        super.show(players);
    }

    /**
     * Open the GUIFastCraft for its associated player.
     */
    public void show() {
        show(player);
    }

    @Override
    public void onClose(HumanEntity closedBy) {
        if (getInventory().getViewers().isEmpty()) {
            dispose();
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        guis.remove(player.getUniqueId());
    }

    @Override
    public void updateLayout() {
        LayoutRecipes curRecipesLayout = craftLayout.getCurRecipesLayout();
        curRecipesLayout.updateRecipes();
        btnPagePrev.setVisible(!curRecipesLayout.isPageFirst());
        btnPageNext.setVisible(!curRecipesLayout.isPageLast());
        super.updateLayout();
    }

    /**
     * Get the player being shown this GUI.
     *
     * @return Returns the player being shown this gui.
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Show a tab in the GUI.
     *
     * @param tab The tab to show.
     */
    private void showTab(CraftingTab tab) {
        btnTabCrafting.setGlowing(tab == CraftingTab.CRAFTING);
        btnTabArmor.setGlowing(tab == CraftingTab.ARMOR);
        btnTabRepair.setGlowing(tab == CraftingTab.REPAIR);
        btnTabFireworks.setGlowing(tab == CraftingTab.FIREWORKS);

        craftLayout.showLayout(tab);
        updateLayout();
    }


    private boolean btnPagePrevClick(GUIButton.ButtonClick info) {
        craftLayout.getCurRecipesLayout().prevPage();
        updateLayout();
        return true;
    }

    private boolean btnPageNextClick(GUIButton.ButtonClick info) {
        craftLayout.getCurRecipesLayout().nextPage();
        updateLayout();
        return true;
    }

    private boolean btnMultiCraftClick(GUIButton.ButtonClick info) {
        return false; // TODO
    }

    private boolean btnWorkbenchClick(GUIButton.ButtonClick info) {
        player.openWorkbench(location, true); // TODO Don't force
        return true;
    }

    private boolean btnTabCraftingClick(GUIButton.ButtonClick info) {
        showTab(CraftingTab.CRAFTING);
        return true;
    }

    private boolean btnTabArmorClick(GUIButton.ButtonClick info) {
        showTab(CraftingTab.ARMOR);
        return true;
    }

    private boolean btnTabRepairClick(GUIButton.ButtonClick info) {
        showTab(CraftingTab.REPAIR);
        return true;
    }

    private boolean btnTabFireworksClick(GUIButton.ButtonClick info) {
        showTab(CraftingTab.FIREWORKS);
        return true;
    }

    private void inventoryChange() {
        updateLayout();
    }

    public static class GUIListener implements Listener {
        @EventHandler(priority = EventPriority.MONITOR)
        public void onInventoryClick(InventoryClickEvent e) {
            if (!e.isCancelled()) invChange(e.getWhoClicked());
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onInventoryDrag(InventoryDragEvent e) {
            if (!e.isCancelled()) invChange(e.getWhoClicked());
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onInventoryPickup(InventoryPickupItemEvent e) {
            if (e.isCancelled()) return;
            for (HumanEntity he : e.getInventory().getViewers()) {
                invChange(he);
            }
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPlayerPickupItem(PlayerPickupItemEvent e) {
            if (!e.isCancelled()) invChange(e.getPlayer());
        }

        /**
         * Notify GUI's that the inventory has changed.
         *
         * @param player The player whose inventory was changed.
         */
        private void invChange(HumanEntity player) {
            GUIFastCraft gui = guis.get(player.getUniqueId());
            if (gui != null) {
                FastCraft fc = FastCraft.getInstance();
                Bukkit.getScheduler().scheduleSyncDelayedTask(fc, () -> gui.inventoryChange(), 1L);
            }
        }
    }
}
