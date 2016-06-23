package co.kepler.fastcraftplus.updater;

import org.bukkit.Bukkit;

import java.io.*;
import java.net.URLConnection;

/**
 * Downloads FastCraft+ releases.
 */
public class ReleaseDownloader {
    private static final int DOWNLOAD_BUFFER = 1024 * 5;
    private static final String JAR_FILENAME = "FastCraftPlus";

    private final Release release;

    /**
     * Instantiate a new ReleaseDownloader with a runnable to be executed on download completion.
     *
     * @param release The release to download.
     */
    public ReleaseDownloader(Release release) {
        this.release = release;
    }

    /**
     * Asynchronously download the release.
     */
    public void downloadAsync(DownloadListener listener) {
        new Thread(new DownloadRunnable(this, listener)).start();
    }

    /**
     * Download the release.
     */
    public void download(DownloadListener listener) {
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
                listener.onProgressChange(downloaded += bytes, fileSize);
            }

            // Close streams
            inputStream.close();
            outputStream.close();

            // Notify listener
            listener.onDownloadComplete(updateFile);
        } catch (IOException e) {
            e.printStackTrace();

            // Unable to download successfully
            listener.onDownloadComplete(null);
        }
    }

    /**
     * Called by the ReleaseDownloader when a download completes.
     */
    public interface DownloadListener {

        /**
         * Called when the download completes.
         *
         * @param file The downloaded file. Null if unsuccessfully downloaded.
         */
        void onDownloadComplete(File file);

        /**
         * Called when the download progress changes.
         *
         * @param downloaded The number of bytes downloaded.
         * @param total      The total number of bytes.
         */
        void onProgressChange(int downloaded, int total);
    }

    /**
     * Runnable that calls the download() method of a ReleaseDownloader.
     */
    private class DownloadRunnable implements Runnable {
        private final ReleaseDownloader downloader;
        private final DownloadListener listener;

        /**
         * Create a new instance of DownloadRunnable.
         *
         * @param downloader The downloader whose download() method will be run.
         */
        public DownloadRunnable(ReleaseDownloader downloader, DownloadListener listener) {
            this.downloader = downloader;
            this.listener = listener;
        }

        @Override
        public void run() {
            downloader.download(listener);
        }
    }
}
