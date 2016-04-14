package co.kepler.fastcraftplus.commands;

import co.kepler.fastcraftplus.FastCraft;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.List;

/**
 * Handles the debug command. Used to provide information for bug reports.
 */
public class CmdDebug extends SimpleCommand {
    private static final String USAGE = "/fastcraftadmin debug";

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        if (args.length != 0) {
            sender.sendMessage(FastCraft.lang().commands_usage(USAGE));
        } else if (!sender.equals(Bukkit.getConsoleSender())) {
            sender.sendMessage(FastCraft.lang().commands_consoleOnly());
        } else {
            outputDebug();
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return null;
    }

    /**
     * Write debug info to the console.
     */
    private void outputDebug() {
        StringBuilder output = new StringBuilder("FastCraft+ Debug Info:\n\n");
        output.append("========== FastCraft+ Debug Info ==========\n");

        // Output FastCraft+, Server, and Java versions
        output.append("Versions:\n")
                .append("  FastCraft+ v").append(FastCraft.getInstance().getDescription().getVersion()).append('\n')
                .append("  ").append(Bukkit.getVersion()).append('\n')
                .append("  ").append(Bukkit.getBukkitVersion()).append('\n')
                .append("  Java ").append(System.getProperty("java.version")).append("\n");

        // Output plugins and versions
        Plugin[] plugins = Bukkit.getPluginManager().getPlugins();
        output.append("Other Plugins (").append(plugins.length - 1).append("):\n");
        for (Plugin plugin : plugins) {
            if (plugin.equals(FastCraft.getInstance())) continue; // Don't output FastCraft+ here
            output.append("  ").append(plugin.getName()).append(" ")
                    .append(plugin.getDescription().getVersion()).append('\n');
        }

        output.append("===========================================\n");
        FastCraft.log(output.toString());

    }
}
