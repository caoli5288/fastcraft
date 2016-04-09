package co.kepler.fastcraftplus.config;

import co.kepler.fastcraftplus.FastCraft;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

/**
 * To be used by configuration classes with an internal and external config.
 */
public abstract class ConfigExternal extends Config {
    protected final YamlConfiguration config;
    protected final File configFile;

    /**
     * Create a config that is stored in the plugin folder.
     *
     * @param resPath  The path of the config resource.
     * @param filePath The path of the resource file, relative to the plugin folder.
     */
    public ConfigExternal(String resPath, String filePath) {
        super(resPath);
        config = new YamlConfiguration();
        configFile = new File(FastCraft.getInstance().getDataFolder(), filePath);
        load();
    }

    /**
     * Create a config that is stored in the plugin folder.
     *
     * @param resPath  The path of the config resource, and the resource
     *                 file, relative to the plugin folder.
     */
    public ConfigExternal(String resPath) {
        this(resPath, resPath);
    }

    /**
     * Load the config, creating the external file if necessary.
     */
    public void load() {
        try {
            // Create the configuration file if it doesn't already exist
            if (!configFile.exists()) {
                File parent = configFile.getParentFile();
                if (parent.mkdirs()) FastCraft.log("Created directory: " + parent);
                Files.copy(FastCraft.getInstance().getResource(resPath), configFile.toPath());
                FastCraft.log("Created config: " + configFile.getName());
            }

            // Load the config
            InputStream stream = new FileInputStream(configFile);
            config.load(stream);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

        // Set header and defaults
        config.setDefaults(internalConfig);
        config.options().header(internalConfig.options().header());

        // Save the config
        save();
    }

    /**
     * Save the config to its external file.
     */
    protected void save() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
