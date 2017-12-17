package co.kepler.fastcraft.updater;

import co.kepler.fastcraft.api.gui.GUI;

import java.util.List;

/**
 * The GUI used to download FastCraft releases.
 */
public class UpdateGUI extends GUI {

    /**
     * Create a new instance of UpdateGUI
     */
    public UpdateGUI() {
        super("FastCraft Releases", 6);
    }

    public void setReleases(List<Release> releases) {

    }
}
