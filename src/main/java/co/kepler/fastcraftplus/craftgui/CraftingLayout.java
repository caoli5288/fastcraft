package co.kepler.fastcraftplus.craftgui;

import co.kepler.fastcraftplus.gui.*;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * The button layout for the FastCraft GUI.
 */
public class CraftingLayout extends GUIMultiLayout {
    private static final ChatColor NAME_COLOR = ChatColor.GREEN;

    private final FastCraftGUI gui;

    private final GUILayout layoutNavbar;
    private final GUIPagedLayout layoutMain;
    private final GUIPagedLayout layoutArmor;
    private final GUIPagedLayout layoutRepair;
    private final GUIPagedLayout layoutFireworks;

    public CraftingLayout(FastCraftGUI gui) {
        super(new GUIPagedLayout(), new GUILayout(), gui.getHeight() - 1);

        this.gui = gui;

        // Initialize layout pages
        layoutNavbar = getBottomLayout();
        layoutMain = (GUIPagedLayout) getTopLayout();
        layoutArmor = new GUIPagedLayout();
        layoutRepair = new GUIPagedLayout();
        layoutFireworks = new GUIPagedLayout();

        // Create button items
        ItemStack btnMainItem = new GUIItemBuilder(Material.CHEST)
                .setDisplayName(NAME_COLOR + "Basic Crafting")
                .build();

        ItemStack btnArmorItem = new GUIItemBuilder(Material.LEATHER_CHESTPLATE)
                .setDisplayName(NAME_COLOR + "Dyed Armor")
                .build();

        ItemStack btnRepairItem = new GUIItemBuilder(Material.IRON_PICKAXE)
                .setDisplayName(NAME_COLOR + "Repair Items")
                .build();

        ItemStack btnFireworksItem = new GUIItemBuilder(Material.FIREWORK)
                .setDisplayName(NAME_COLOR + "Fireworks")
                .build();

        // Create buttons
        GUIButton btnMain = new GUIButton(btnMainItem);
        btnMain.setClickAction(clickAction -> System.out.println("btnMain clicked!"));
        layoutNavbar.setPendingButton(1, btnMain);

        GUIButton btnArmor = new GUIButton(btnArmorItem);
        btnMain.setClickAction(clickAction -> System.out.println("btnArmor clicked!"));
        layoutNavbar.setPendingButton(2, btnArmor);

        GUIButton btnRepair = new GUIButton(btnRepairItem);
        btnRepair.setClickAction(clickAction -> System.out.println("btnRepair clicked!"));
        layoutNavbar.setPendingButton(3, btnRepair);

        GUIButton btnFireworks = new GUIButton(btnFireworksItem);
        btnFireworks.setClickAction(clickAction -> System.out.println("btnFireworks clicked!"));
        layoutNavbar.setPendingButton(4, btnFireworks);

        // Setup the GUI
        gui.setLayout(this);
        gui.updateLayout();
    }

}
