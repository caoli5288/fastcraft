package co.kepler.fastcraftplus.config;

import co.kepler.fastcraftplus.FastCraft;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.InputStream;

/**
 * To be used by configuration classes that access an internal config, only.
 */
public abstract class Config {
    protected YamlConfiguration internalConfig = new YamlConfiguration();
    protected String resPath = null;

    protected void setInternalConfig(String resPath) {
        if (resPath != null) {
            InputStream resStream = FastCraft.getInstance().getResource(resPath);
            internalConfig = YamlConfiguration.loadConfiguration(resStream);
        } else {
            internalConfig = new YamlConfiguration();
        }
    }

}
