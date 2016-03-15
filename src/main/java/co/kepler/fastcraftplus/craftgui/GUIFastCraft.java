package co.kepler.fastcraftplus.craftgui;

import co.kepler.fastcraftplus.gui.GUI;
import co.kepler.fastcraftplus.gui.GUIButton;
import co.kepler.fastcraftplus.gui.GUIItemBuilder;
import co.kepler.fastcraftplus.gui.GUILayout;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * The FastCraft crafting GUI.
 */
public class GUIFastCraft extends GUI {
    private static final ChatColor BUTTON_NAME_COLOR = ChatColor.GREEN;

    private final GUILayoutCrafting fastCraftLayout;
    private final Player player;

    /**
     * Create a new instance of a FastCraft GUI.
     *
     * @param player The player who will be shown this GUI.
     */
    public GUIFastCraft(Player player) {
        super("FastCraft", 6); // TODO Localize

        this.player = player;

        fastCraftLayout = new GUILayoutCrafting();
        setLayout(fastCraftLayout);

        // Create button items
        ItemStack btnMainItem = new GUIItemBuilder(Material.CHEST)
                .setDisplayName(BUTTON_NAME_COLOR + "Crafting")
                .build();

        ItemStack btnArmorItem = new GUIItemBuilder(Material.LEATHER_CHESTPLATE)
                .setDisplayName(BUTTON_NAME_COLOR + "Dyed Armor")
                .build();

        ItemStack btnRepairItem = new GUIItemBuilder(Material.IRON_PICKAXE)
                .setDisplayName(BUTTON_NAME_COLOR + "Repair Items")
                .build();

        ItemStack btnFireworksItem = new GUIItemBuilder(Material.FIREWORK)
                .setDisplayName(BUTTON_NAME_COLOR + "Fireworks")
                .build();

        // Create buttons
        GUILayout navbar = fastCraftLayout.getLayoutNavbar();

        GUIButton btnMain = new GUIButton(btnMainItem);
        btnMain.setClickAction(clickAction -> System.out.println("btnMain clicked!"));
        navbar.setButton(1, btnMain);

        GUIButton btnArmor = new GUIButton(btnArmorItem);
        btnMain.setClickAction(clickAction -> System.out.println("btnArmor clicked!"));
        navbar.setButton(2, btnArmor);

        GUIButton btnRepair = new GUIButton(btnRepairItem);
        btnRepair.setClickAction(clickAction -> System.out.println("btnRepair clicked!"));
        navbar.setButton(3, btnRepair);

        GUIButton btnFireworks = new GUIButton(btnFireworksItem);
        btnFireworks.setClickAction(clickAction -> System.out.println("btnFireworks clicked!"));
        navbar.setButton(4, btnFireworks);

        System.out.println(fastCraftLayout.getLayoutTab(GUILayoutCrafting.CraftingTab.CRAFTING).getButton(0));

        // Update the GUI's layout
        updateLayout();
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
}
