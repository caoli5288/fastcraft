package co.kepler.fastcraft.commands;

import co.kepler.fastcraft.FastCraft;
import co.kepler.fastcraft.Permissions;
import co.kepler.fastcraft.craftgui.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

/**
 * Handles the toggle command.
 */
public class CmdToggle extends SimpleCommand {
    private static final String USAGE = "/fastcraft toggle [on|off|toggle] [player]";
    private static final String ON = "on", OFF = "off", TOGGLE = "toggle";
    private static final List<String> TOGGLE_TYPE = Arrays.asList(ON, OFF, TOGGLE);

    @Override
    @SuppressWarnings("deprecation")
    public boolean onCommand(CommandSender sender, String[] args) {
        if (args.length > 0) args[0] = args[0].toLowerCase();
        if (args.length > 2 || (args.length > 0 && !TOGGLE_TYPE.contains(args[0]))) {
            sender.sendMessage(FastCraft.lang().commands_usage(USAGE));
            return true;
        } else if (!sender.hasPermission(Permissions.COMMAND_TOGGLE)) {
            sender.sendMessage(FastCraft.lang().commands_noPerm(Permissions.COMMAND_TOGGLE));
            return true;
        }

        String toggleType = args.length > 0 ? args[0] : TOGGLE;
        String togglePlayerName = args.length > 1 ? args[1] : null;

        Player togglePlayer;
        if (togglePlayerName == null) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(FastCraft.lang().commands_playerOnly());
                return true;
            }
            togglePlayer = (Player) sender;
        } else {
            togglePlayer = Bukkit.getPlayer(togglePlayerName);
            if (togglePlayer == null) {
                sender.sendMessage(FastCraft.lang().commands_unknownPlayer(togglePlayerName));
                return true;
            }
            if (togglePlayer == sender) {
                togglePlayerName = null;
            }
        }

        // Toggle enabled
        boolean enabled = true;
        if (toggleType.equals(OFF)) {
            enabled = false;
        } else if (toggleType.equals(TOGGLE)) {
        }

        // Send message to toggling player
        if (togglePlayerName != null) {
            // If toggling another player
            if (!sender.hasPermission(Permissions.COMMAND_TOGGLE_OTHER)) {
                sender.sendMessage(FastCraft.lang().commands_noPerm(Permissions.COMMAND_TOGGLE_OTHER));
                return true;
            }
            String name = togglePlayer.getName();
            if (enabled) {
                togglePlayer.sendMessage(FastCraft.lang().commands_fastcraft_toggle_output_on_other(name));
            } else {
                togglePlayer.sendMessage(FastCraft.lang().commands_fastcraft_toggle_output_off_other(name));
            }
        }

        // Send message to toggled player
        if (enabled) {
            togglePlayer.sendMessage(FastCraft.lang().commands_fastcraft_toggle_output_on_self());
        } else {
            togglePlayer.sendMessage(FastCraft.lang().commands_fastcraft_toggle_output_off_self());
        }

        // Set player preferences
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return tabMatch(args[0], TOGGLE_TYPE);
        } else if (args.length == 2) {
            return tabMatchPlayer(args[1]);
        }
        return null;
    }


}
