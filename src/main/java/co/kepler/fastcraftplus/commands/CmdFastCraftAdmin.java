package co.kepler.fastcraftplus.commands;

import co.kepler.fastcraftplus.FastCraftPlus;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

/**
 * Handles the fastcraftadmin command.
 */
public class CmdFastCraftAdmin extends SimpleCommand {
    private static final String USAGE = "/fastcraftadmin <reload|debug>";
    private static final String RELOAD = "reload", DEBUG = "debug";
    private static final List<String> ARGS = Arrays.asList(RELOAD, DEBUG);

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
        sender.sendMessage(FastCraftPlus.lang().commands_usage(USAGE));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length > 1) {
            switch (args[0].toLowerCase()) {
            case RELOAD:
                return cmdReload.onTabComplete(sender, popFirst(args));
            case DEBUG:
                return cmdDebug.onTabComplete(sender, popFirst(args));
            }
            return null;
        }
        return tabMatch(args[0], ARGS);
    }
}
