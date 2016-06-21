package co.kepler.fastcraftplus.commands;

import co.kepler.fastcraftplus.FastCraftPlus;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Handles the debug command. Used to provide information for bug reports.
 */
public class CmdDebug extends SimpleCommand {
    private static final String USAGE = "/fastcraftadmin debug";

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        if (args.length != 0) {
            sender.sendMessage(FastCraftPlus.lang().commands_usage(USAGE));
        } else if (!sender.equals(Bukkit.getConsoleSender())) {
            sender.sendMessage(FastCraftPlus.lang().commands_consoleOnly());
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
                .append("  FastCraft+ v").append(FastCraftPlus.getInstance().getDescription().getVersion()).append('\n')
                .append("  ").append(Bukkit.getVersion()).append('\n')
                .append("  ").append(Bukkit.getBukkitVersion()).append('\n')
                .append("  Java ").append(System.getProperty("java.version")).append("\n");

        // Output plugins and versions
        List<Plugin> plugins = getSortedPlugins();
        output.append("Other Plugins (").append(plugins.size() - 1).append("):\n");
        for (Plugin plugin : plugins) {
            if (plugin.equals(FastCraftPlus.getInstance())) continue; // Don't output FastCraft+ here
            output.append("  ").append(plugin.getName()).append(" ")
                    .append(plugin.getDescription().getVersion()).append('\n');
        }

        output.append("===========================================\n");
        FastCraftPlus.log(output.toString());
    }

    /**
     * Gets all plugins on the server, sorted alphabetically.
     *
     * @return Returns a sorted list of the server's plugins.
     */
    public List<Plugin> getSortedPlugins() {
        List<Plugin> result = Arrays.asList(Bukkit.getPluginManager().getPlugins());
        Collections.sort(result, new Comparator<Plugin>() {
            @Override
            public int compare(Plugin p0, Plugin p1) {
                return p0.getName().compareToIgnoreCase(p1.getName());
            }
        });
        return result;
    }
}
