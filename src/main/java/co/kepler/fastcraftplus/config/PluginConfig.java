package co.kepler.fastcraftplus.config;

import co.kepler.fastcraftplus.FastCraft;

import java.util.Arrays;

/**
 * A class for access to the FastCraft+ configuration.
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
    }

    public String getLanguage() {
        return config.getString("language");
    }

    public boolean getDefaultEnabled() {
        return config.getBoolean("default-enabled");
    }

    public boolean getToolbar_gap() {
        return config.getBoolean("toolbar.gap");
    }

    public int getToolbar_layout_pagePrev() {
        return config.getInt("toolbar.layout.page-prev", -1);
    }

    public int getToolbar_layout_pageNext() {
        return config.getInt("toolbar.layout.page-next", -1);
    }

    public int getToolbar_layout_multiplier() {
        return config.getInt("toolbar.layout.multiplier", -1);
    }

    public int getToolbar_layout_workbench() {
        return config.getInt("toolbar.layout.workbench", -1);
    }

    public int getToolbar_layout_refresh() {
        return config.getInt("toolbar.layout.refresh", -1);
    }

    public int[] getToolbar_multiplierOrder() {
        return multOrder;
    }
}
