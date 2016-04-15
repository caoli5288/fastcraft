package co.kepler.fastcraftplus.config;

import java.util.List;

/**
 * A class for access to the FastCraft+ configuration.
 */
public class PluginConfig extends ConfigExternal {

    public PluginConfig() {
        super(true, true);
        setConfigs("config.yml");
    }

    public String getLanguage() {
        return config.getString("language");
    }

    public boolean getDefaultEnabled() {
        return config.getBoolean("default-enabled");
    }

    public List<String> getCommandCompat() {
        return config.getStringList("command-compat");
    }
}
