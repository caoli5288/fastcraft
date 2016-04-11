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
     * @param resPath The path of the config resource, and the resource
     *                file, relative to the plugin folder.
     */
    public ConfigExternal(String resPath) {
        this(resPath, resPath);
    }

    /**
     * Load the config, creating the external file if necessary.
     */
    public void load() {
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
            InputStream stream = new FileInputStream(configFile);
            config.load(stream);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Save the comments at the top of the default config to the config file.
     */
    private void saveHeader() throws IOException {
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
