package co.kepler.fastcraftplus.config;

import co.kepler.fastcraftplus.FastCraft;
import org.bukkit.configuration.Configuration;

/**
 * A class for access to the FastCraft+ configuration.
 */
public class Config {
    private Configuration config;

    public Config() {
        loadConfig();
    }

    /**
     * Loads config with default values, and saves to update the config.
     */
    public void loadConfig() {
        FastCraft fastCraft = FastCraft.getInstance();
        fastCraft.saveDefaultConfig();
        config = fastCraft.getConfig();
        config.options().copyDefaults(true);
        fastCraft.saveConfig();
    }

    public String getLanguage() {
        return config.getString("language");
    }
}
