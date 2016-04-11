package co.kepler.fastcraftplus.commands;

import co.kepler.fastcraftplus.FastCraft;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Registers commands, and handles execution and tab completion.
 */
public class CommandManager implements CommandExecutor, TabCompleter {
    private Map<String, SimpleCommand> commandMap = new HashMap<>();

    /**
     * Create a new instance of the command manager.
     */
    public CommandManager() {
        commandMap.put("fastcraft",      new CmdFastCraft());
        commandMap.put("fastcraftadmin", new CmdFastCraftAdmin());
        commandMap.put("craft",          new CmdCraft());
    }

    /**
     * Register this as the command executor for all FastCraft+ commands.
     */
    public void registerCommands() {
        FastCraft fc = FastCraft.getInstance();
        for (String command : commandMap.keySet()) {
            fc.getCommand(command).setExecutor(this);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command c, String s, String[] args) {
        // Get the command if it exists, otherwise return false
        SimpleCommand command = commandMap.get(c.getName());
        if (command == null) return false;

        // Execute the command, and return true
        command.onCommand(sender, args);command.popFirst(new Integer[0]);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command c, String s, String[] args) {
        // Get the command if it exists, otherwise return false
        SimpleCommand command = commandMap.get(c.getName());
        if (command == null) return Collections.emptyList();

        // Tab complete the command
        return command.onTabComplete(sender, args);
    }
}
