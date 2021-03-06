package co.kepler.fastcraft.config;

import co.kepler.fastcraft.FastCraft;
import co.kepler.fastcraft.util.BukkitUtil;

import java.util.Arrays;
import java.util.List;

/**
 * A class for access to the FastCraft configuration.
 */
public class PluginConfig extends ConfigExternal {
    private int[] multOrder = new int[]{1, 2, 3, 4, 6, 8, 12, 16, 24, 32, 48, 64};

    public PluginConfig() {
        super(true, true);
        setConfigs("config.yml");
    }

    @Override
    public void load() {
        super.load();

        // Load multOrder
        try {
            String[] multOrderStr = config.getString("toolbar.multiplier-order").replaceAll("\\s", "").split(",");
            int[] newMultOrder = new int[multOrderStr.length];
            for (int i = 0; i < multOrderStr.length; i++) {
                newMultOrder[i] = Integer.parseInt(multOrderStr[i]);
            }
            Arrays.sort(multOrder = newMultOrder);
        } catch (NumberFormatException e) {
            FastCraft.err("Invalid integer for toolbar.multiplier-order in config.yml: " + e.getMessage());
        }

        // Remove automatic-updates
        if (config.getConfigurationSection("automatic-updates") != null) {
            config.set("automatic-updates", null);
            try {
                BukkitUtil.saveConfiguration(config, configFile);
                saveHeader();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public String language() {
        return config.getString("language");
    }

    public boolean defaultEnabled() {
        return config.getBoolean("default-enabled");
    }

    public boolean toolbar_gap() {
        return config.getBoolean("toolbar.gap");
    }

    public int toolbar_layout_pagePrev() {
        return config.getInt("toolbar.layout.page-prev");
    }

    public int toolbar_layout_pageNext() {
        return config.getInt("toolbar.layout.page-next");
    }

    public int toolbar_layout_multiplier() {
        return config.getInt("toolbar.layout.multiplier");
    }

    public int toolbar_layout_workbench() {
        return config.getInt("toolbar.layout.workbench");
    }

    public int toolbar_layout_refresh() {
        return config.getInt("toolbar.layout.refresh");
    }

    public int[] toolbar_multiplierOrder() {
        return multOrder;
    }

    public String automaticUpdates_type() {
        return config.getString("automatic-updates.type");
    }

    public double automaticUpdates_interval() {
        return config.getDouble("automatic-updates.interval");
    }

    public List<String> automaticUpdates_commands() {
        return config.getStringList("automatic-updates.commands");
    }

    public boolean debug() {
        return config.getBoolean("debug");
    }
}
