package co.kepler.fastcraftplus.commands;

import co.kepler.fastcraftplus.FastCraft;
import co.kepler.fastcraftplus.Permission;
import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * Handles the reload command.
 */
public class CmdReload extends SimpleCommand {
    private SimpleCommand cmdReload = new CmdReload();

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        if (args.length != 0) {
            sender.sendMessage(FastCraft.lang().commands_fastcraftadmin_reload_output());
        } else if (!sender.hasPermission(Permission.ADMIN_RELOAD)) {
            sender.sendMessage(FastCraft.lang().commands_noPerm(Permission.ADMIN_RELOAD));
        } else {
            FastCraft.loadConfigs();
            sender.sendMessage(FastCraft.lang().commands_fastcraftadmin_reload_output());
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return null;
    }
}
