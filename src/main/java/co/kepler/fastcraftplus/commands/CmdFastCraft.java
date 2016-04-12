package co.kepler.fastcraftplus.commands;

import co.kepler.fastcraftplus.FastCraft;
import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * Handles the fastcraft command.
 */
public class CmdFastCraft extends SimpleCommand {
    private final SimpleCommand cmdCraft = new CmdCraft();
    private final SimpleCommand cmdToggle = new CmdToggle();

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        if (args.length > 0) {
            switch (args[0].toLowerCase()) {
            case "craft":
                return cmdCraft.onCommand(sender, popFirst(args));
            case "toggle":
                return cmdToggle.onCommand(sender, popFirst(args));
            }
        }
        sender.sendMessage(FastCraft.lang().commands_fastcraft_usage());
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return null; // TODO
    }
}
