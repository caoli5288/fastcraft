package co.kepler.fastcraftplus.commands;

import co.kepler.fastcraftplus.FastCraft;
import co.kepler.fastcraftplus.Permissions;
import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * Handles the reload command.
 */
public class CmdReload extends SimpleCommand {
    private static final String USAGE = "/fastcraftadmin reload";

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        if (args.length != 0) {
            sender.sendMessage(FastCraft.lang().commands_usage(USAGE));
        } else if (!sender.hasPermission(Permissions.ADMIN_RELOAD)) {
            sender.sendMessage(FastCraft.lang().commands_noPerm(Permissions.ADMIN_RELOAD));
        } else {
            FastCraft.getInstance().reload();
            sender.sendMessage(FastCraft.lang().commands_fastcraftadmin_reload_output());
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return null;
    }
}
