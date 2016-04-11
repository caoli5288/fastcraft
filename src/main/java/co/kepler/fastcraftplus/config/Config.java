package co.kepler.fastcraftplus.config;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

/**
 * To be used by configuration classes that access an internal config, only.
 */
public abstract class Config {
    protected YamlConfiguration internalConfig = new YamlConfiguration();
    protected String resPath = null;

    protected void setInternalConfig(String resPath) {
        if (resPath != null) {
            File resFile = new File(getClass().getResource(resPath).getFile());
            internalConfig = YamlConfiguration.loadConfiguration(resFile);
        } else {
            internalConfig = new YamlConfiguration();
        }
    }

}
