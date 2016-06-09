package co.kepler.fastcraftplus.config;

import co.kepler.fastcraftplus.FastCraft;
import co.kepler.fastcraftplus.util.BukkitUtil;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.nio.file.Files;

/**
 * To be used by configuration classes with an internal and external config.
 */
public abstract class ConfigExternal extends Config {
    protected YamlConfiguration config;
    protected File configFile = null;
    private final boolean copyHeader, copyDefaults;

    /**
     * Create a config that is stored in the plugin folder.
     *
     * @param copyHeader   Whether the comment header should be copied from the internal file.
     * @param copyDefaults Whether default values from the internal config should be copied.
     */
    public ConfigExternal(boolean copyHeader, boolean copyDefaults) {
        this.copyHeader = copyHeader;
        this.copyDefaults = copyDefaults;
    }

    /**
     * Set the external config file.
     *
     * @param filePath The path of the file within the FastCraft+ plugin directory.
     */
    protected void setExternalConfig(String filePath) {
        configFile = filePath == null ? null : new File(FastCraft.getInstance().getDataFolder(), filePath);
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
        } else {
            try {
                // Save the config header, or save the config if it doesn't exist
                if (!configFile.exists()) {
                    File parent = configFile.getParentFile();
                    if (parent.mkdirs()) FastCraft.log("Created directory: " + parent);
                    Files.copy(FastCraft.getInstance().getResource(resPath), configFile.toPath());
                    FastCraft.log("Created config: " + configFile.getName());
                }

                // Load the config
                config = BukkitUtil.loadConfiguration(new FileInputStream(configFile));

                // Save default values and copy comment header
                if (copyDefaults) saveDefaults();
                if (copyHeader) saveHeader();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Set default values
        config.setDefaults(internalConfig);
    }

    /**
     * Save the comments at the top of the default config to the config file.
     */
    private void saveHeader() throws IOException {
        if (resPath == null || configFile == null) return;

        StringBuilder newFileStr = new StringBuilder();
        InputStream stream;
        BufferedReader reader;
        String curLine;

        // Read in header from internal config
        stream = FastCraft.getInstance().getResource(resPath);
        reader = new BufferedReader(new InputStreamReader(stream, ENCODING));
        while ((curLine = reader.readLine()) != null && curLine.startsWith("#")) {
            newFileStr.append(curLine).append('\n');
        }
        newFileStr.append('\n');
        reader.close();

        // Read in config values from external config
        stream = new FileInputStream(configFile);
        reader = new BufferedReader(new InputStreamReader(stream, ENCODING));
        curLine = reader.readLine();
        while (curLine != null && curLine.startsWith("#")) curLine = reader.readLine(); // Skip header comments
        while (curLine != null && curLine.matches("\\s*")) curLine = reader.readLine(); // Skip empty lines
        while (curLine != null) { // Append the rest of the file
            newFileStr.append(curLine).append('\n');
            curLine = reader.readLine();
        }
        reader.close();

        // Write the file
        FileOutputStream outputStream = new FileOutputStream(configFile);
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, ENCODING));
        writer.write(newFileStr.toString());
        writer.close();
    }

    private void saveDefaults() {
        if (resPath == null || configFile == null) return;

        config.setDefaults(internalConfig);
        config.options().copyDefaults(true);
        BukkitUtil.saveConfiguration(config, configFile);
    }
}
