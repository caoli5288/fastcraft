package co.kepler.fastcraftplus.updater;

import java.io.File;

/**
 * Downloads FastCraft+ releases.
 */
public class ReleaseDownloader {
    private final Release release;
    private final DownloadListener listener;

    private File downloadedFile;

    /**
     * Instantiate a new ReleaseDownloader with a runnable to be executed on download completion.
     *
     * @param release  The release to download.
     * @param listener Runnable to be executed
     */
    public ReleaseDownloader(Release release, DownloadListener listener) {
        this.release = release;
        this.listener = listener;
    }

    /**
     * Get the downloaded file.
     *
     * @return Returns the downloaded file, or null if it hasn't been downloaded.
     */
    public File getDownloadedFile() {
        return downloadedFile;
    }

    /**
     * Asynchronously download the release.
     */
    private void downloadAsync() {
        new Thread(new DownloadRunnable(this)).start();
    }

    /**
     * Download the release.
     */
    private void download() {

    }

    /**
     * Called by the ReleaseDownloader when a download completes.
     */
    public interface DownloadListener {

        /**
         * Called when the download completes.
         *
         * @param downloader The downloader that finished downloading.
         */
        void onDownloadComplete(ReleaseDownloader downloader);

        /**
         * Called when the download progress changes.
         *
         * @param progress The download progress, between 0 and 1.
         */
        void onProgressChange(double progress);
    }

    /**
     * Runnable that calls the download() method of a ReleaseDownloader.
     */
    private class DownloadRunnable implements Runnable {
        private final ReleaseDownloader downloader;

        /**
         * Create a new instance of DownloadRunnable.
         *
         * @param downloader The downloader whose download() method will be run.
         */
        public DownloadRunnable(ReleaseDownloader downloader) {
            this.downloader = downloader;
        }

        @Override
        public void run() {
            downloader.download();
        }
    }
}
