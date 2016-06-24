package co.kepler.fastcraftplus.updater;

import co.kepler.fastcraftplus.FastCraftPlus;

/**
 * Checks for updates, and downloads them if one's available.
 */
public class Updater {
    private static int taskID = -1;
    private static UpdateType updateType;

    public static void load() {
        updateType = UpdateType.fromConfig();

    }

    private class UpdateChecker implements Runnable {
        @Override
        public void run() {

        }
    }

    enum UpdateType {
        NONE, PATCH, STABLE, NEWEST;

        public boolean canUpdate(Release from, Release to) {
            Release.Version vFrom = from.version, vTo = to.version;
            if (vFrom.compareTo(vTo) <= 0) return false; // Only update to newer versions

            switch (this) {
            case NONE:
                return false;
            case PATCH:
                return vFrom.major == vTo.major
                        && vFrom.minor == vTo.minor
                        && vFrom.patch < vTo.patch;
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
