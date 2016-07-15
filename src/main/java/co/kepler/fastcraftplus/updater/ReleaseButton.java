package co.kepler.fastcraftplus.updater;

import co.kepler.fastcraftplus.FastCraftPlus;
import co.kepler.fastcraftplus.api.gui.GUI;
import co.kepler.fastcraftplus.api.gui.GUIButton;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * Provides details about a FastCraft+ release, and when clicked, downloads/installs releases.
 */
public class ReleaseButton extends GUIButton implements Release.DownloadListener {
    private static final ItemStack
    ITEM_CURRENT = new ItemStack(Material.GOLD_BLOCK),
    ITEM_DOWNLOADING = new ItemStack(Material.DIAMOND_AXE);

    private static final Map<Release.Stability, ItemStack> stabilityItems = new HashMap<>();
    static {
        stabilityItems.put(Release.Stability.STABLE, new ItemStack(Material.EMERALD_BLOCK));
        stabilityItems.put(Release.Stability.UNSTABLE, new ItemStack(Material.REDSTONE_BLOCK));
        stabilityItems.put(Release.Stability.UNKNOWN, new ItemStack(Material.IRON_BLOCK));
    }

    private final Release release;
    double progress = -1;

    public ReleaseButton(Release release) {
        this.release = release;
    }

    @Override
    public ItemStack getItem() {
        ItemStack result;

        if (progress >= 0) {
            result = ITEM_DOWNLOADING.clone();
            double damage = progress * result.getType().getMaxDurability();
            result.setDurability((short) Math.max(1, damage));
        } else if (FastCraftPlus.version().equals(release.version)) {
            result = ITEM_CURRENT.clone();
        } else {
            result = stabilityItems.get(release.stability).clone();
        }

        // TODO DisplaynName and Lore

        return result;
    }

    @Override
    public boolean isVisible() {
        return true;
    }

    @Override
    public boolean onClick(GUI gui, InventoryClickEvent invEvent) {
        // TODO
        return true;
    }

    @Override
    public void onDownloadStart(Release release) {
        progress = 0;
    }

    @Override
    public void onProgressChange(Release release, int downloaded, int total) {
        progress = (double) downloaded / total;
    }

    @Override
    public void onDownloadComplete(Release release) {
        progress = -1;
    }
}
