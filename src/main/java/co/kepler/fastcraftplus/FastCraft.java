package co.kepler.fastcraftplus;

import co.kepler.fastcraftplus.gui.GUI;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class FastCraft extends JavaPlugin implements Listener {
    private static FastCraft instance;

    @Override
    public void onEnable() {
        instance = this;

        Bukkit.getPluginManager().registerEvents(new TestListener(), this);
    }

    @Override
    public void onDisable() {
        GUI.disposeAll();
    }

    /**
     * Get an instance of FastCraft.
     * @return Returns an instance of FastCraft.
     */
    public static FastCraft getInstance() {
        return instance;
    }
}
