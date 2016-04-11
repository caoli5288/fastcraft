package co.kepler.fastcraftplus.config;

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
