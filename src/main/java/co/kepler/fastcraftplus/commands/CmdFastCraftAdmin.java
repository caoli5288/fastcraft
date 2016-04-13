package co.kepler.fastcraftplus.commands;

import co.kepler.fastcraftplus.FastCraft;
import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * Handles the fastcraftadmin command.
 */
public class CmdFastCraftAdmin extends SimpleCommand {
    private static final String USAGE = "/fastcraftadmin <reload|debug>";

    private final SimpleCommand cmdReload = new CmdReload();
    private final SimpleCommand cmdDebug = new CmdDebug();

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        if (args.length > 0) {
            switch (args[0].toLowerCase()) {
            case "reload":
                return cmdReload.onCommand(sender, popFirst(args));
            case "debug":
                return cmdDebug.onCommand(sender, popFirst(args));
            }
        }
        sender.sendMessage(FastCraft.lang().commands_usage(USAGE));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return null; // TODO
    }
}
