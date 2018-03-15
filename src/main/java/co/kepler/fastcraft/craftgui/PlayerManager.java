package co.kepler.fastcraft.craftgui;

import co.kepler.fastcraft.FastCraft;
import co.kepler.fastcraft.Permissions;
import co.kepler.fastcraft.util.BukkitUtil;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manages players preferences, and handles opening crafting GUI's.
 */
public class PlayerManager implements Listener {

    /**
     * Handles player interactions with workbench blocks.
     *
     * @param e The player interact event.
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.isCancelled()) return;
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (e.getClickedBlock().getType() != Material.WORKBENCH) return;
        if (e.getPlayer().isSneaking()) return;

        // See if the player has permission to open a FastCraft interface
        Player player = e.getPlayer();
        if (!player.hasPermission(Permissions.USE)) return;

        // If the player has FastCraft enabled
        if (!Prefs.getPrefs(player).isFastCraftEnabled()) return;

        // Cancel the event, and open the GUI
        e.setCancelled(true);
        new GUIFastCraft(player, e.getClickedBlock().getLocation(), false).show();
    }

    public static class Prefs {
        private static final String KEY_ENABLED = "enabled";

        private static Map<UUID, Prefs> prefs = new HashMap<>();

        private YamlConfiguration conf = new YamlConfiguration();

        private static File getPrefsFile(UUID uuid) throws IOException {
            File playerFile = new File(FastCraft.getInstance().getDataFolder(), "preferences/" + uuid + ".yml");
            File parentDir = playerFile.getParentFile();
            if (parentDir.mkdirs()) FastCraft.log("Created directory: " + parentDir);
            if (playerFile.createNewFile()) FastCraft.log("Created player file: " + playerFile);
            return playerFile;
        }

        public static Prefs getPrefs(Player player) {
            UUID uuid = player.getUniqueId();
            if (prefs.containsKey(uuid)) return prefs.get(uuid);

            Prefs result = new Prefs();
            try {
                File playerFile = getPrefsFile(uuid);
                BukkitUtil.loadConfiguration(new FileInputStream(playerFile), result.conf);
            } catch (IOException e) {
                e.printStackTrace();
            }

            prefs.put(uuid, result);
            return result;
        }

        public static void saveAllPrefs() {
            for (UUID uuid : prefs.keySet()) {
                try {
                    File prefsFile = getPrefsFile(uuid);
                    BukkitUtil.saveConfiguration(prefs.get(uuid).conf, prefsFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public boolean isFastCraftEnabled() {
            return conf.getBoolean(KEY_ENABLED, FastCraft.config().defaultEnabled());
        }

        public void setFastCraftEnabled(boolean enabled) {
            conf.set(KEY_ENABLED, enabled);
        }
    }
}
