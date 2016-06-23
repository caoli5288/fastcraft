package co.kepler.fastcraftplus.updater;

import org.bukkit.Bukkit;

import java.io.*;
import java.net.URLConnection;

/**
 * Downloads FastCraft+ releases.
 */
public class ReleaseDownloader {
    private static final int DOWNLOAD_BUFFER = 1024;
    private static final String JAR_FILENAME = "FastCraftPlus";

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
        try {
            // Open a URL connection for the release
            URLConnection connection = release.url.openConnection();
            int fileSize = connection.getContentLength();

            // Get the file for the download
            File updateFile = new File(Bukkit.getUpdateFolder(), JAR_FILENAME + ".jar");
            if (updateFile.exists() && !updateFile.delete()) {
                // If update file exists and unable to delete
                int curIndex = 1;
                while (updateFile.exists()) {
                    updateFile = new File(Bukkit.getUpdateFolder(), JAR_FILENAME + " (" + curIndex++ + ").jar");
                }
            }

            // Create the data streams
            BufferedInputStream inputStream = new BufferedInputStream(connection.getInputStream());
            BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(updateFile));
            byte[] data = new byte[DOWNLOAD_BUFFER];

            // Download the file
            int bytes, downloaded = 0;
            while ((bytes = inputStream.read(data)) >= 0) {
                outputStream.write(data, 0, bytes);
                listener.onProgressChange((double) (downloaded += bytes) / fileSize);
            }

            // Close streams
            inputStream.close();
            outputStream.close();

            // Notify listener
            listener.onDownloadComplete(true);
        } catch (IOException e) {
            e.printStackTrace();

            // Unable to download successfully
            listener.onDownloadComplete(false);
        }
    }

    /**
     * Called by the ReleaseDownloader when a download completes.
     */
    public interface DownloadListener {

        /**
         * Called when the download completes.
         */
        void onDownloadComplete(boolean successful);

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
