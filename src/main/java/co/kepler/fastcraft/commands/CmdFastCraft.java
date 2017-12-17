package co.kepler.fastcraft.commands;

import co.kepler.fastcraft.FastCraft;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

/**
 * Handles the fastcraft command.
 */
public class CmdFastCraft extends SimpleCommand {
    private static final String USAGE = "/fastcraft <craft|toggle>";
    private static final String CRAFT = "craft", TOGGLE = "toggle";
    private static final List<String> ARGS = Arrays.asList(CRAFT, TOGGLE);

    private final SimpleCommand cmdCraft = new CmdCraft();
    private final SimpleCommand cmdToggle = new CmdToggle();

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        if (args.length > 0) {
            switch (args[0].toLowerCase()) {
            case CRAFT:
                return cmdCraft.onCommand(sender, popFirst(args));
            case TOGGLE:
                return cmdToggle.onCommand(sender, popFirst(args));
            }
        }
        sender.sendMessage(FastCraft.lang().commands_usage(USAGE));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length > 1) {
            switch (args[0].toLowerCase()) {
            case CRAFT:
                return cmdCraft.onTabComplete(sender, popFirst(args));
            case TOGGLE:
                return cmdToggle.onTabComplete(sender, popFirst(args));
            }
            return null;
        }
        return tabMatch(args[0], ARGS);
    }
}
