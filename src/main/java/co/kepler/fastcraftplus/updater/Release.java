package co.kepler.fastcraftplus.updater;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ben on 6/14/2016.
 */
public class Release {
    private static final String RELEASES_URL =
            "https://raw.githubusercontent.com/BenWoodworth/FastCraftPlus/master/releases.xml";

    public final Version version;
    public final Stability stability;
    public final URL url;

    public Release(Version version, Stability stability, URL url) {
        this.version = version;
        this.stability = stability;
        this.url = url;
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
            NodeList releaseNodes = doc.getDocumentElement().getChildNodes();
            for (int i = 0; i < releaseNodes.getLength(); i++) {
                Node relNode = releaseNodes.item(i);
                if (relNode.getNodeType() != Node.ELEMENT_NODE) continue;
                try {
                    NamedNodeMap attributes = relNode.getAttributes();
                    Version version = new Version(attributes.getNamedItem("version").getNodeValue());
                    Stability stable = Stability.fromString(attributes.getNamedItem("stability").getNodeValue());
                    URL url = new URL(attributes.getNamedItem("url").getNodeValue());
                    releases.add(new Release(version, stable, url));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return releases;
        } catch (Exception e) {
            return null;
        }
    }

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
        public String toString() {
            return "v" + major + "." + minor + "." + patch;
        }
    }

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
