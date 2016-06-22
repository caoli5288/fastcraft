package co.kepler.fastcraftplus.updater;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Contains information about a FastCraft+ release.
 */
public class Release implements Comparable<Release> {
    private static final String RELEASES_URL =
            "https://raw.githubusercontent.com/BenWoodworth/FastCraftPlus/master/releases.xml";

    public final Version version;
    public final Stability stability;
    public final URL url;
    public final List<String> changes;

    /**
     * Create a new instance of Release.
     *
     * @param version   This release's version.
     * @param stability The stability of this release.
     * @param url       The URL to this release's .jar file.
     * @param changes   The changes from the previous release.
     */
    public Release(Version version, Stability stability, URL url, List<String> changes) {
        this.version = version;
        this.stability = stability;
        this.url = url;
        this.changes = Collections.unmodifiableList(changes);
    }

    @Override
    public int compareTo(Release release) {
        return version.compareTo(release.version);
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

                    // Get release information
                    Version version = new Version(attributes.getNamedItem("version").getNodeValue());
                    Stability stable = Stability.fromString(attributes.getNamedItem("stable").getNodeValue());
                    URL url = new URL(attributes.getNamedItem("url").getNodeValue());

                    // Loop through the list of changes in this release
                    List<String> changes = new ArrayList<>();
                    NodeList changeNodes = relNode.getChildNodes();
                    for (int j = 0; j < changeNodes.getLength(); j++) {
                        Node changeNode = changeNodes.item(j);
                        if (!changeNode.getNodeName().equals("change")) continue;
                        changes.add(changeNode.getTextContent());
                    }

                    releases.add(new Release(version, stable, url, changes));
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
     * A release version.
     */
    public static class Version implements Comparable<Version> {
        public final int major, minor, patch;

        private Version(String version) {
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
        public String toString() {
            return "v" + major + "." + minor + "." + patch;
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
}
