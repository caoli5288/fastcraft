package co.kepler.fastcraftplus.commands;

import co.kepler.fastcraftplus.FastCraft;
import co.kepler.fastcraftplus.Permission;
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
    private static final String WORKBENCH = "workbench", FASTCRAFT = "fastcraft";
    private static final List<String> types = Arrays.asList(WORKBENCH, FASTCRAFT);

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        if (args.length > 0) args[0] = args[0].toLowerCase();
        if (!(sender instanceof Player)) {
            sender.sendMessage(FastCraft.lang().commands_playerOnly());
        } else if (args.length > 1) {
            sender.sendMessage(FastCraft.lang().commands_usage(USAGE));
        } else if (!sender.hasPermission(Permission.COMMAND_CRAFT)) {
            sender.sendMessage(FastCraft.lang().commands_noPerm(Permission.COMMAND_CRAFT));
        } else if (args.length > 0) {
            open((Player) sender, args[0]);
        } else {
            open((Player) sender);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return null;
    }

    /**
     * Open a FastCraft+ GUI, or a workbench, depending on permissions and preferences.
     *
     * @param player The player opening the inventory.
     */
    private void open(Player player) {
        if (PlayerManager.Prefs.getPrefs(player).isFastCraftEnabled()
                || !player.hasPermission(Permission.USE)) {
            new GUIFastCraft(player, null).show();
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
        if (!types.contains(type)) {
            player.sendMessage(FastCraft.lang().commands_usage(USAGE));
        } else if (type.equals(WORKBENCH)) {
            player.openWorkbench(null, true);
        } else if (!player.hasPermission(Permission.USE)) {
            player.sendMessage(FastCraft.lang().commands_noPerm(Permission.USE));
        } else {
            new GUIFastCraft(player, null).show();
        }
    }
}
