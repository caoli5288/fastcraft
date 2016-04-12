package co.kepler.fastcraftplus.commands;

import co.kepler.fastcraftplus.FastCraft;
import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * Handles the fastcraftadmin command.
 */
public class CmdFastCraftAdmin extends SimpleCommand {
    private final SimpleCommand cmdReload = new CmdReload();

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        if (args.length > 0) {
            switch (args[0].toLowerCase()) {
            case "reload":
                return cmdReload.onCommand(sender, popFirst(args));
            }
        }
        sender.sendMessage(FastCraft.lang().commands_fastcraftadmin_usage());
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return null; // TODO
    }
}
