package co.kepler.fastcraftplus.config;

import co.kepler.fastcraftplus.FastCraft;
import org.bukkit.configuration.Configuration;

/**
 * A class for access to the FastCraft+ configuration.
 */
public class PluginConfig extends ConfigExternal {

    public PluginConfig() {
        super("config.yml");
    }

    public String getLanguage() {
        return config.getString("language");
    }
}
