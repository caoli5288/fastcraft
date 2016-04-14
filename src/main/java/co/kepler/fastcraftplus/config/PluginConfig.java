package co.kepler.fastcraftplus.config;

import java.util.List;

/**
 * A class for access to the FastCraft+ configuration.
 */
public class PluginConfig extends ConfigExternal {

    public PluginConfig() {
        super(true);
        setConfigs("config.yml");
    }

    public String getLanguage() {
        return config.getString("language");
    }

    public List<String> getCommandCompat() {
        return config.getStringList("command-compat");
    }
}
