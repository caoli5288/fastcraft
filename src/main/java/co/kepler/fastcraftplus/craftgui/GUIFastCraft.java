package co.kepler.fastcraftplus.craftgui;

import co.kepler.fastcraftplus.FastCraft;
import co.kepler.fastcraftplus.gui.*;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.HashMap;
import java.util.Map;

/**
 * The FastCraft crafting GUI.
 */
public class GUIFastCraft extends GUI {
    private static final ChatColor BUTTON_NAME_COLOR = ChatColor.GREEN;
    private static final String NOT_YET_IMPLEMENTED = ChatColor.RED + "Not Yet Implemented";

    private static Map<Object, GUIFastCraft> guis; // <Location or UUID, GUIFastCraft>

    private final LayoutFastCraft craftLayout;
    private final Player player;
    private final Location location;

    private final GUIButton btnPagePrev;
    private final GUIButton btnPageNext;
    private final GUIButton btnRefresh;
    private final GUIButton btnCraftingMultiplier;
    private final GUIButton btnWorkbench;
    private final GUIButtonGlowing btnTabCrafting;
    private final GUIButtonGlowing btnTabArmor;
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

        // Create Previous Page button
        btnPagePrev = new GUIButton(new GUIItemBuilder(Material.ARROW)
                .setDisplayName(BUTTON_NAME_COLOR + "Previous Page")
                .setHideInfo(true).build());
        btnPagePrev.setClickAction(new GUIButton.ClickAction() {
            public boolean onClick(GUIButton.Click info) {
                return btnPagePrevClick(info);
            }
        });
        navbar.setButton(1, 0, btnPagePrev);

        // Create Next Page button
        btnPageNext = new GUIButton(new GUIItemBuilder(Material.ARROW)
                .setDisplayName(BUTTON_NAME_COLOR + "Next Page")
                .setHideInfo(true).build());
        btnPageNext.setClickAction(new GUIButton.ClickAction() {
            public boolean onClick(GUIButton.Click info) {
                return btnPageNextClick(info);
            }
        });
        navbar.setButton(1, 8, btnPageNext);

        // Create Refresh button
        btnRefresh = new GUIButton(new GUIItemBuilder(Material.NETHER_STAR)
                .setDisplayName(BUTTON_NAME_COLOR + "Refresh")
                .setHideInfo(true).build());
        btnRefresh.setClickAction(new GUIButton.ClickAction() {
            public boolean onClick(GUIButton.Click info) {
                return btnRefreshClick(info);
            }
        });
        navbar.setButton(1, 5, btnRefresh);

        // Create Crafting Multiplier button
        btnCraftingMultiplier = new GUIButton(new GUIItemBuilder(Material.ANVIL)
                .setDisplayName(BUTTON_NAME_COLOR + "Crafting Multiplier")
                .setLore(NOT_YET_IMPLEMENTED).build());
        btnCraftingMultiplier.setClickAction(new GUIButton.ClickAction() {
            public boolean onClick(GUIButton.Click info) {
                return btnCraftingMultiplierClick(info);
            }
        });
        navbar.setButton(1, 7, btnCraftingMultiplier);

        // Create Workbench button
        btnWorkbench = new GUIButton(new GUIItemBuilder(Material.WORKBENCH)
                .setDisplayName(BUTTON_NAME_COLOR + "Open Crafting Grid").build());
        btnWorkbench.setClickAction(new GUIButton.ClickAction() {
            public boolean onClick(GUIButton.Click info) {
                return btnWorkbenchClick(info);
            }
        });
        navbar.setButton(1, 6, btnWorkbench);

        // Create Crafting button
        btnTabCrafting = new GUIButtonGlowing(new GUIItemBuilder(Material.STICK)
                .setDisplayName(BUTTON_NAME_COLOR + "Crafting")
                .setHideInfo(true).build());
        btnTabCrafting.setClickAction(new GUIButton.ClickAction() {
            public boolean onClick(GUIButton.Click info) {
                return btnTabCraftingClick(info);
            }
        });
        btnTabCrafting.setGlowing(true);
        navbar.setButton(1, 1, btnTabCrafting);

        // Create armor button
        ItemStack coloredChestplate = new GUIItemBuilder(Material.LEATHER_CHESTPLATE)
                .setDisplayName(BUTTON_NAME_COLOR + "Dyed Armor")
                .setLore(NOT_YET_IMPLEMENTED)
                .setHideInfo(true).build();
        LeatherArmorMeta chestplateMeta = (LeatherArmorMeta) coloredChestplate.getItemMeta();
        chestplateMeta.setColor(Color.fromRGB(0x4C72C5));
        coloredChestplate.setItemMeta(chestplateMeta);
        btnTabArmor = new GUIButtonGlowing(coloredChestplate);
        btnTabArmor.setClickAction(new GUIButton.ClickAction() {
            public boolean onClick(GUIButton.Click info) {
                return btnTabArmorClick(info);
            }
        });
        navbar.setButton(1, 2, btnTabArmor);

        // Create Fireworks button
        btnTabFireworks = new GUIButtonGlowing(new GUIItemBuilder(Material.FIREWORK)
                .setDisplayName(BUTTON_NAME_COLOR + "Fireworks")
                .setLore(NOT_YET_IMPLEMENTED)
                .setHideInfo(true).build());
        btnTabFireworks.setClickAction(new GUIButton.ClickAction() {
            public boolean onClick(GUIButton.Click info) {
                return btnTabFireworksClick(info);
            }
        });
        navbar.setButton(1, 3, btnTabFireworks);

        // Update the GUI's layout
        updateLayout();

        // Add to guis
        guis.put(player.getUniqueId(), this);
        guis.put(location, this);
    }

    public static void init() {
        Bukkit.getPluginManager().registerEvents(new GUIListener(), FastCraft.getInstance());
        guis = new HashMap<>();
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
        guis.remove(location);
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
        btnTabFireworks.setGlowing(tab == CraftingTab.FIREWORKS);

        craftLayout.showLayout(tab);
        updateLayout();
    }


    private boolean btnPagePrevClick(GUIButton.Click info) {
        craftLayout.getCurRecipesLayout().prevPage();
        updateLayout();
        return true;
    }

    private boolean btnPageNextClick(GUIButton.Click info) {
        craftLayout.getCurRecipesLayout().nextPage();
        updateLayout();
        return true;
    }

    private boolean btnRefreshClick(GUIButton.Click info) {
        craftLayout.getCurRecipesLayout().clearButtons();
        updateLayout();
        return true;
    }

    private boolean btnCraftingMultiplierClick(GUIButton.Click info) {
        return false; // TODO
    }

    private boolean btnWorkbenchClick(GUIButton.Click info) {
        player.openWorkbench(location, true); // TODO Don't force
        return true;
    }

    private boolean btnTabCraftingClick(GUIButton.Click info) {
        showTab(CraftingTab.CRAFTING);
        return true;
    }

    private boolean btnTabArmorClick(GUIButton.Click info) {
        showTab(CraftingTab.ARMOR);
        return true;
    }

    private boolean btnTabFireworksClick(GUIButton.Click info) {
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
            final GUIFastCraft gui = guis.get(player.getUniqueId());
            if (gui != null) {
                FastCraft fc = FastCraft.getInstance();
                Bukkit.getScheduler().scheduleSyncDelayedTask(fc, new Runnable() {
                    public void run() {
                        gui.inventoryChange();
                    }
                }, 1L);
            }
        }

        @EventHandler(priority = EventPriority.HIGHEST)
        public void onPlayerInteract(PlayerInteractEvent e) {
            if (e.isCancelled()) return;
            if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
            if (e.getClickedBlock().getType() != Material.WORKBENCH) return;

            // Cancel the interaction, and show the FastCraft GUI.
            e.setCancelled(true);
            new GUIFastCraft(e.getPlayer(), e.getClickedBlock().getLocation()).show();
        }

        @EventHandler(priority = EventPriority.HIGHEST)
        public void onBlockBreak(BlockBreakEvent e) {
            if (e.isCancelled()) return;
            blockRemoved(e.getBlock());
        }

        @EventHandler(priority = EventPriority.HIGHEST)
        public void onBlockBurn(BlockBurnEvent e) {
            if (e.isCancelled()) return;
            blockRemoved(e.getBlock());
        }

        @EventHandler(priority = EventPriority.HIGHEST)
        public void onEntityExplode(EntityExplodeEvent e) {
            if (e.isCancelled()) return;
            for (Block b : e.blockList()) {
                blockRemoved(b);
            }
        }

        private void blockRemoved(Block b) {
            if (b.getType() != Material.WORKBENCH) return;
            GUIFastCraft gui = guis.get(b.getLocation());
            if (gui != null) {
                gui.dispose();
            }
        }
    }
}
