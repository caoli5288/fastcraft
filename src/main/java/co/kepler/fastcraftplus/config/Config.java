package co.kepler.fastcraftplus.config;

import co.kepler.fastcraftplus.FastCraft;
import co.kepler.fastcraftplus.util.BukkitUtil;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.InputStream;

/**
 * To be used by configuration classes that access an internal config, only.
 */
public abstract class Config {
    public static final String ENCODING = "UTF-8";

    protected YamlConfiguration internalConfig = new YamlConfiguration();
    protected String resPath = null;

    /**
     * Set the internal config.
     *
     * @param resPath The path of the internal config.
     */
    protected void setInternalConfig(String resPath) {
        this.resPath = resPath;
        if (resPath != null) {
            InputStream resStream = FastCraft.getInstance().getResource(resPath);
            internalConfig = BukkitUtil.loadConfiguration(resStream);
        } else {
            internalConfig = new YamlConfiguration();
        }
    }
}
