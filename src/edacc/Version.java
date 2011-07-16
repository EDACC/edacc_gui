package edacc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author simon
 */
public class Version {
    private static final Pattern pattern = Pattern.compile("v([0-9]+)\\.([0-9]+)(?:\\.([0-9]+))?");
    
    private int major,minor,patch;
    private String branch;
    private String location;
    
    public Version() {
        
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(EDACCApp.class).getContext().getResourceMap(Version.class);
        major = resourceMap.getInteger("version.major");
        minor = resourceMap.getInteger("version.minor");
        patch = resourceMap.getInteger("version.patch");
        branch = resourceMap.getString("version.branch");
    }
    
    public Version(String str_version, String location) {
        Matcher m = pattern.matcher(str_version);
        if (m.matches()) {
            major = Integer.parseInt(m.group(1));
            minor = Integer.parseInt(m.group(2));
            patch = m.group(3) == null ? 0 : Integer.parseInt(m.group(3));
        }
        this.location = location;
        branch = org.jdesktop.application.Application.getInstance(edacc.EDACCApp.class).getContext().getResourceMap(Version.class).getString("version.branch");
    }
    
    public int compareTo(Version version) {
        if (version.major > major) {
            return -1;
        } else if (version.major < major) {
            return 1;
        } else if (version.minor > minor) {
            return -1;
        } else if (version.minor < minor) {
            return 1;
        } else if (version.patch > patch) {
            return -1;
        } else if (version.patch < patch) {
            return 1;
        }
        return 0;
    }

    public String getLocation() {
        return location;
    }

    @Override
    public String toString() {
        String res = "v" + major + "." + minor;
        if (patch != 0) {
            res += "." + patch;
        }
        res += " " + branch;
        return res;
    }
}
