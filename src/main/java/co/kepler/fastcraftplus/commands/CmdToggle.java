package co.kepler.fastcraftplus.commands;

import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * Handles the toggle command.
 */
public class CmdToggle extends SimpleCommand {

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return null;
    }
}
