package co.kepler.fastcraftplus.updater;

import co.kepler.fastcraftplus.FastCraftPlus;
import com.google.common.io.Files;
import org.bukkit.Bukkit;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.bind.DatatypeConverter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Contains information about a FastCraft+ release.
 */
public class Release implements Comparable<Release> {
    private static final String RELEASES_URL = "http://fcp-releases.benwoodworth.net/";
    private static final File RELEASES_DIR = new File(FastCraftPlus.getInstance().getDataFolder(), "releases");
    private static final String JAR_FILENAME = "FastCraftPlus v%s.jar";
    private static final int DOWNLOAD_BUFFER = 1024 * 8;

    private static Release installedRelease = null;

    public final Version version;
    public final Stability stability;
    public final URL url;
    public final String md5;
    public final List<String> changes;

    /**
     * Create a new instance of Release.
     *
     * @param version   This release's version.
     * @param stability The stability of this release.
     * @param url       The URL to this release's .jar file.
     * @param md5       This MD5 hash of this release's jar file.
     * @param changes   The changes from the previous release.
     */
    public Release(Version version, Stability stability, URL url, String md5, List<String> changes) {
        this.version = version;
        this.stability = stability;
        this.url = url;
        this.md5 = md5;
        this.changes = Collections.unmodifiableList(changes);
    }

    @Override
    public int compareTo(Release release) {
        return version.compareTo(release.version);
    }

    @Override
    public String toString() {
        return "FastCraft+ v" + version;
    }

    /**
     * Get the release jar file in the FastCraft+ plugin directory.
     *
     * @return Returns the release jar file.
     */
    public File getReleaseFile() {
        return new File(RELEASES_DIR, String.format(JAR_FILENAME, version));
    }

    /**
     * See if the release has been downloaded.
     *
     * @return Returns true if the release has been downloaded.
     */
    public boolean isDownloaded() {
        return getReleaseFile().exists();
    }

    /**
     * Copy the release to the update directory.
     *
     * @throws IOException Thrown if an IOException occurs.
     */
    public void install() throws IOException {
        File updateDir = Bukkit.getUpdateFolderFile();
        String filename = FastCraftPlus.getJarFile().getName();
        Files.copy(getReleaseFile(), new File(updateDir, filename));
        installedRelease = this;
    }

    /**
     * See which release has been installed to the update directory.
     *
     * @return Returns the installed release, or null if none were installed.
     */
    public static Release getInstalledRelease() {
        return installedRelease;
    }

    /**
     * See if this release is newer than the installed release.
     *
     * @return Returns true if this release is newer.
     */
    public boolean isNewerThanInstalledRelease() {
        return installedRelease == null || compareTo(installedRelease) > 0;
    }

    /**
     * Fetch a list of releases.
     *
     * @return Returns a list of releases, or null if unable to do so.
     */
    public static List<Release> fetchReleases() {
        try {
            List<Release> releases = new ArrayList<>();
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.parse(new URL(RELEASES_URL).openStream());

            // Loop through all releases
            NodeList releaseNodes = doc.getDocumentElement().getChildNodes();
            for (int i = 0; i < releaseNodes.getLength(); i++) {
                Node relNode = releaseNodes.item(i);
                if (relNode.getNodeType() != Node.ELEMENT_NODE) continue;
                try {
                    NamedNodeMap attributes = relNode.getAttributes();

                    // Get release information strings
                    String versionStr = attr(attributes, "version");
                    String stableStr = attr(attributes, "stable");
                    String urlStr = attr(attributes, "url");
                    String md5 = attr(attributes, "md5");
                    if (versionStr == null || stableStr == null || urlStr == null || md5 == null) {
                        FastCraftPlus.debug("Unable to fetch release with invalid attributes:");
                        FastCraftPlus.debug(String.format("version=%1s, stable=%2s, url=%3s, md5=%4s",
                                versionStr, stableStr, urlStr, md5));
                        continue;
                    }

                    // Get release information
                    Version version = new Version(versionStr);
                    Stability stable = Stability.fromString(stableStr);
                    URL url = new URL(urlStr);

                    // Loop through the list of changes in this release
                    List<String> changes = new ArrayList<>();
                    NodeList changeNodes = relNode.getChildNodes();
                    for (int j = 0; j < changeNodes.getLength(); j++) {
                        Node changeNode = changeNodes.item(j);
                        if (!changeNode.getNodeName().equals("change")) continue;
                        changes.add(changeNode.getTextContent());
                    }

                    releases.add(new Release(version, stable, url, md5, changes));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return releases;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Helpful method to get attribute values.
     *
     * @param attributes The attributes map.
     * @param attribute  The attribute to get the value of.
     * @return Returns the value of the attribute.
     */
    private static String attr(NamedNodeMap attributes, String attribute) {
        if (attributes == null || attribute == null) return null;
        Node namedItem = attributes.getNamedItem(attribute);
        if (namedItem == null) return null;
        return namedItem.getNodeValue();
    }

    /**
     * Asynchronously download the release.
     */
    public void downloadAsync(final DownloadListener listener) {
        new Thread(new Runnable() {
            public void run() {
                Release.this.download(listener);
            }
        }).start();
    }

    /**
     * Download the release.
     */
    public void download(DownloadListener listener) {
        File releaseFile = getReleaseFile();
        listener.onDownloadStart(this);
        try {
            // Get the file for the download
            if (releaseFile.getParentFile().mkdirs()) FastCraftPlus.log("Created releases directory");
            if (!releaseFile.createNewFile()) {
                listener.onDownloadComplete(this);
                return; // Return if already downloaded
            }

            // Open a URL connection for the release
            URLConnection connection = url.openConnection();
            int fileSize = connection.getContentLength();

            // Create the data streams
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            DigestInputStream digest = new DigestInputStream(connection.getInputStream(), messageDigest);
            BufferedInputStream inputStream = new BufferedInputStream(digest);
            BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(releaseFile));
            byte[] data = new byte[DOWNLOAD_BUFFER];

            // Download the file
            int bytes, downloaded = 0;
            listener.onProgressChange(this, 0, fileSize);
            while ((bytes = inputStream.read(data)) >= 0) {
                outputStream.write(data, 0, bytes);
                listener.onProgressChange(this, downloaded += bytes, fileSize);
            }

            // Close streams
            digest.close();
            outputStream.close();

            // Validate hash
            byte[] md5 = digest.getMessageDigest().digest();
            String downloadHash = DatatypeConverter.printHexBinary(md5);

            // Notify listener
            if (downloadHash.equalsIgnoreCase(this.md5)) {
                listener.onDownloadComplete(this);
                return;
            } else {
                listener.onDownloadFail(this, "MD5 hash mismatch.");
            }

            // Notify listener
        } catch (IOException e) {
            e.printStackTrace();

            // Unable to download successfully
            listener.onDownloadFail(this, e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        // Delete file if download unsuccessful
        releaseFile.delete();
    }

    /**
     * A release version.
     */
    public static class Version implements Comparable<Version> {
        public final int major, minor, patch;

        public Version(String version) {
            String[] split = version.split("\\.");
            major = Integer.parseInt(split[0]);
            minor = split.length > 1 ? Integer.parseInt(split[1]) : 0;
            patch = split.length > 2 ? Integer.parseInt(split[2]) : 0;
        }

        @Override
        public int compareTo(Version version) {
            if (major != version.major) return major - version.major;
            if (minor != version.minor) return minor - version.minor;
            return patch - version.patch;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof Version && compareTo((Version) o) == 0;
        }

        @Override
        public int hashCode() {
            int result = major;
            result = result * 31 + minor;
            result = result * 31 + patch;
            return result;
        }

        @Override
        public String toString() {
            return major + "." + minor + (patch == 0 ? "" : "." + patch);
        }
    }

    /**
     * The stability of a release.
     */
    public enum Stability {
        STABLE, UNSTABLE, UNKNOWN;

        public static Stability fromString(String stable) {
            switch (stable.toLowerCase()) {
            case "true":
                return STABLE;
            case "false":
                return UNSTABLE;
            default:
                return UNKNOWN;
            }
        }
    }

    /**
     * Called by the ReleaseDownloader when a download completes.
     */
    public interface DownloadListener {
        /**
         * Called when the download starts.
         *
         * @param release The release being downloaded.
         */
        void onDownloadStart(Release release);

        /**
         * Called when the download progress changes.
         *
         * @param release    The release being downloaded.
         * @param downloaded The number of bytes downloaded.
         * @param total      The total number of bytes.
         */
        void onProgressChange(Release release, int downloaded, int total);

        /**
         * Called when the download completes.
         *
         * @param release The release being downloaded.
         */
        void onDownloadComplete(Release release);

        /**
         * Called when the release failed to download.
         *
         * @param release The release that failed to download.
         * @param message Message with information about the failure.
         */
        void onDownloadFail(Release release, String message);
    }
}
