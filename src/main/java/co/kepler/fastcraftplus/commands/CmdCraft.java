package co.kepler.fastcraftplus.commands;

import co.kepler.fastcraftplus.FastCraftPlus;
import co.kepler.fastcraftplus.Permissions;
import co.kepler.fastcraftplus.craftgui.GUIFastCraft;
import co.kepler.fastcraftplus.craftgui.PlayerManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

/**
 * Handles the craft command.
 */
public class CmdCraft extends SimpleCommand {
    private static final String USAGE = "/fastcraft craft [workbench|fastcraft]";
    private static final String WORKBENCH = "workbench", FASTCRAFT = "fastcraft", HASH = "fastcraft*";
    private static final List<String> TYPES = Arrays.asList(WORKBENCH, FASTCRAFT);

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        if (args.length > 0) args[0] = args[0].toLowerCase();
        if (!(sender instanceof Player)) {
            sender.sendMessage(FastCraftPlus.lang().commands_playerOnly());
        } else if (args.length > 1) {
            sender.sendMessage(FastCraftPlus.lang().commands_usage(USAGE));
        } else if (!sender.hasPermission(Permissions.COMMAND_CRAFT)) {
            sender.sendMessage(FastCraftPlus.lang().commands_noPerm(Permissions.COMMAND_CRAFT));
        } else if (args.length > 0) {
            open((Player) sender, args[0]);
        } else {
            open((Player) sender);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length != 1) return null;
        return tabMatch(args[0], TYPES);
    }

    /**
     * Open a FastCraft+ GUI, or a workbench, depending on permissions and preferences.
     *
     * @param player The player opening the inventory.
     */
    private void open(Player player) {
        if (PlayerManager.Prefs.getPrefs(player).isFastCraftEnabled()
                && player.hasPermission(Permissions.USE)) {
            new GUIFastCraft(player, null, false).show();
        } else {
            player.openWorkbench(null, true);
        }
    }

    /**
     * Open a FastCraft+ GUI, or a workbench, depending on what was specified.
     *
     * @param player The player opening the inventory.
     * @param type   The type of inventory to open.
     */
    private void open(Player player, String type) {
        if (!TYPES.contains(type) && !type.equals(HASH)) {
            player.sendMessage(FastCraftPlus.lang().commands_usage(USAGE));
        } else if (type.equals(WORKBENCH)) {
            player.openWorkbench(null, true);
        } else if (!player.hasPermission(Permissions.USE)) {
            player.sendMessage(FastCraftPlus.lang().commands_noPerm(Permissions.USE));
        } else {
            new GUIFastCraft(player, null, type.equals(HASH)).show();
        }
    }
}
