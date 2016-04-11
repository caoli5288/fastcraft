package co.kepler.fastcraftplus.config;

import co.kepler.fastcraftplus.BukkitUtil;
import co.kepler.fastcraftplus.FastCraft;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * To be used by configuration classes that access an internal config, only.
 */
public abstract class Config {
    protected YamlConfiguration internalConfig = new YamlConfiguration();
    protected String resPath = null;

    /**
     * Set the internal config.
     *
     * @param resPath The path of the internal config.
     */
    protected void setInternalConfig(String resPath) {
        if (resPath != null) {
            InputStream resStream = FastCraft.getInstance().getResource(resPath);
            Reader reader = new InputStreamReader(resStream);
            internalConfig = BukkitUtil.loadConfiguration(reader);
        } else {
            internalConfig = new YamlConfiguration();
        }
    }

}
