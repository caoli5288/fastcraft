package co.kepler.fastcraftplus.config;

import co.kepler.fastcraftplus.FastCraft;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.InputStream;

/**
 * To be used by configuration classes that access an internal config, only.
 */
public abstract class Config {
    protected final YamlConfiguration internalConfig;
    protected final String resPath;

    /**
     * Create a new Config from a resource.
     *
     * @param resPath The path of the config resource.
     */
    public Config(String resPath) {
        this.resPath = resPath;
        InputStream resStream = FastCraft.getInstance().getResource(resPath);
        internalConfig = YamlConfiguration.loadConfiguration(resStream);
    }
}
