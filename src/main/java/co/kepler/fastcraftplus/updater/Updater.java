package co.kepler.fastcraftplus.updater;

import co.kepler.fastcraftplus.FastCraftPlus;

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

    }

    private class UpdateChecker implements Runnable {
        @Override
        public void run() {
            // Get release candidates
            List<Release> releases = Release.fetchReleases();
            if (releases == null) return;
            Collections.sort(releases);
            for (Iterator<Release> iter = releases.iterator(); iter.hasNext();) {
                if (!updateType.canUpdate(iter.next())) iter.remove();
            }

            // Get release to download
            if (releases.isEmpty()) return;
            Release release = releases.get(0);

            // TODO Download release
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
