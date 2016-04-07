package co.kepler.fastcraftplus.commands;

import org.bukkit.command.*;

import java.util.*;

/**
 * A command class that contains sub-commands.
 */
public abstract class CompositeCommand implements CommandExecutor, TabCompleter{
    private final String name;
    private final Map<String, CompositeCommand> subCommands = new HashMap<>();

    public CompositeCommand(String name) {
        this.name = name;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        LinkedList<String> args = new LinkedList<>();
        Collections.addAll(args, strings);

        return onCommand(commandSender, args);
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {

    }

    protected boolean onCommand(CommandSender sender, LinkedList<String> args) {
        if (args.size() > 0) {
            CompositeCommand command = subCommands.get(args.getFirst().toLowerCase());
            if (command != null) {
                LinkedList<String> newArgs = new LinkedList<>(args);
                newArgs.removeFirst();
                return command.onCommand(sender, newArgs);
            }
        }
        return runCommand(sender, args);
    }

    protected List<String> onTabComplete(CommandSender sender, String toComplete) {

    }

    public void addSubCommand(CompositeCommand command) {
        subCommands.put(command.getName().toLowerCase(), command);
    }

    public String getName() {
        return name;
    }

    public abstract boolean runCommand(CommandSender sender, LinkedList<String> args);
}
