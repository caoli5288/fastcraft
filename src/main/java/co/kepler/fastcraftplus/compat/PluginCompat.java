package co.kepler.fastcraftplus.compat;

/**
 * An interface to be used by plugin compatibility classes.
 */
public interface PluginCompat {

    /**
     * Called when the plugin compatibility is loaded
     */
    public void init();

    /**
     * Get the name of the plugin.
     *
     * @return Returns the name of the plugin.
     */
    public String getPluginName();
}
