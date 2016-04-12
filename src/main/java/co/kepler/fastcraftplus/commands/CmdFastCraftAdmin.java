package co.kepler.fastcraftplus.commands;

import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * Handles the fastcraftadmin command.
 */
public class CmdFastCraftAdmin extends SimpleCommand {

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return null;
    }
}
