package co.kepler.fastcraftplus.updater;

import co.kepler.fastcraftplus.FastCraftPlus;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.File;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Checks for updates, and downloads them if one's available.
 */
public class Updater {
    private static final Release.Version VERSION = new Release.Version(
            FastCraftPlus.getInstance().getDescription().getVersion());

    private static int taskID = -1;
    private static UpdateType updateType;

    public static void load() {
        updateType = UpdateType.fromConfig();

        // Cancel update check task, and start a new one.
        BukkitScheduler sched = Bukkit.getScheduler();
        sched.cancelTask(taskID);
        taskID = -1;
        if (updateType != UpdateType.NONE) {
            long interval = FastCraftPlus.config().automaticUpdates_interval() * 60 * 60 * 20;
            UpdateChecker checker = new UpdateChecker();
            taskID = sched.scheduleSyncRepeatingTask(FastCraftPlus.getInstance(), checker, 1L, interval);
        }
    }

    private static class UpdateChecker implements Runnable {
        @Override
        public void run() {
            // Get release candidates
            List<Release> releases = Release.fetchReleases();
            if (releases == null) return;
            Collections.sort(releases);
            for (Iterator<Release> iter = releases.iterator(); iter.hasNext(); ) {
                if (!updateType.canUpdate(iter.next())) iter.remove();
            }

            // Get release to download
            if (releases.isEmpty()) return;
            Release release = releases.get(0);

            // Download release
            release.download(new AutoDownloadListener());
        }
    }

    private static class AutoDownloadListener implements Release.DownloadListener {

        @Override
        public void onDownloadComplete(Release release, File file) {
            System.out.println("Finished downloading FastCraft+ " + release);
        }

        @Override
        public void onProgressChange(Release release, int downloaded, int total) {
            double percent = (int) (1000. * downloaded / total) / 10.;
            System.out.println("Downloading FastCraft+ update:" + percent + "%");
        }
    }

    enum UpdateType {
        NONE, PATCH, STABLE, NEWEST;

        public boolean canUpdate(Release to) {
            Release.Version vTo = to.version;
            if (VERSION.compareTo(vTo) <= 0) return false; // Only update to newer versions

            switch (this) {
            case NONE:
                return false;
            case PATCH:
                return VERSION.major == vTo.major
                        && VERSION.minor == vTo.minor
                        && VERSION.patch < vTo.patch;
            case STABLE:
                return to.stability == Release.Stability.STABLE;
            case NEWEST:
            default:
                return true;
            }
        }

        public static UpdateType fromConfig() {
            String type = FastCraftPlus.config().automaticUpdates_type();
            try {
                return valueOf(type.toUpperCase());
            } catch (IllegalArgumentException ignored) {
                FastCraftPlus.err("Invalid automatic update type: '" + type + "'! Using 'NONE' instead");
                return NONE;
            }
        }
    }
}
