package co.kepler.fastcraftplus.config;

import co.kepler.fastcraftplus.FastCraft;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.nio.file.Files;

/**
 * To be used by configuration classes with an internal and external config.
 */
public abstract class ConfigExternal extends Config {
    protected YamlConfiguration config;
    protected File configFile = null;
    private final boolean copyHeader;

    /**
     * Create a config that is stored in the plugin folder.
     *
     * @param copyHeader Whether the comment header should be copied from the internal file.
     */
    public ConfigExternal(boolean copyHeader) {
        this.copyHeader = copyHeader;
    }

    /**
     * Set the external config file.
     *
     * @param filePath The path of the file within the FastCraft+ plugin directory.
     */
    protected void setExternalConfig(String filePath) {
        configFile = filePath == null ? null : new File(FastCraft.getInstance().getDataFolder(), filePath);
        load();
    }

    protected void setConfigs(String path) {
        setInternalConfig(path);
        setExternalConfig(path);
    }

    /**
     * Load the config, creating the external file if necessary.
     */
    public void load() {
        if (configFile == null) {
            config = new YamlConfiguration();
            return;
        }
        try {
            // Save the config header, or save the config if it doesn't exist
            if (configFile.exists()) {
                saveHeader();
            } else {
                File parent = configFile.getParentFile();
                if (parent.mkdirs()) FastCraft.log("Created directory: " + parent);
                Files.copy(FastCraft.getInstance().getResource(resPath), configFile.toPath());
                FastCraft.log("Created config: " + configFile.getName());
            }

            // Load the config
            config.load(configFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Save the comments at the top of the default config to the config file.
     */
    private void saveHeader() throws IOException {
        if (!copyHeader || resPath == null || configFile == null) return;

        StringBuilder newFileStr = new StringBuilder();
        InputStream stream;
        BufferedReader reader;
        String curLine;

        // Read in header from internal config
        stream = FastCraft.getInstance().getResource(resPath);
        reader = new BufferedReader(new InputStreamReader(stream));
        while ((curLine = reader.readLine()) != null && curLine.startsWith("#")) {
            newFileStr.append(curLine).append('\n');
        }
        newFileStr.append('\n');
        reader.close();

        // Read in config values from external config
        stream = new FileInputStream(configFile);
        reader = new BufferedReader(new InputStreamReader(stream));
        curLine = reader.readLine();
        while (curLine != null && curLine.startsWith("#")) curLine = reader.readLine(); // Skip header comments
        while (curLine != null && curLine.matches("\\s*")) curLine = reader.readLine(); // Skip empty lines
        while (curLine != null) { // Append the rest of the file
            newFileStr.append(curLine).append('\n');
            curLine = reader.readLine();
        }
        reader.close();

        // Write the file
        FileWriter writer = new FileWriter(configFile);
        writer.write(newFileStr.toString());
        writer.close();
    }
}
