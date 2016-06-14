package co.kepler.fastcraftplus.updater;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * Created by Ben on 6/14/2016.
 */
public class SpigotDownload {
    private static final String DATE_FORMAT = "MMM d, yyyy 'at' h:m a";
    private static final String HISTORY_URL = "https://www.spigotmc.org/resources/%s/history";

    public SpigotDownload(Element element) {

    }

    public static List<SpigotDownload> getDownloads(String resourceID)
            throws ParserConfigurationException, IOException, SAXException {

        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = builder.parse(new URL(String.format(HISTORY_URL, resourceID)).openStream());

    }

    public static class Version implements Comparable<Version> {
        public final int major, minor, patch;

        private Version(String version) {
            String[] split = version.substring(1).split("\\.");
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
    }
}
