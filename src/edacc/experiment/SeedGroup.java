package edacc.experiment;

/**
 * 3-Tuple (seed group, instance id, run number) used in seed group mapping
 * @author daniel
 */
public class SeedGroup {
    private int seedGroup;
    private int instanceId;
    private int run;

    /**
     * Creates the 3-tuple seed group
     * @param seedGroup the seed group as <code>int</code>
     * @param instanceId the instance id
     * @param run the run
     */
    public SeedGroup(int seedGroup, int instanceId, int run) {
        this.seedGroup = seedGroup;
        this.instanceId = instanceId;
        this.run = run;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SeedGroup other = (SeedGroup) obj;
        if (this.seedGroup != other.seedGroup) {
            return false;
        }
        if (this.instanceId != other.instanceId) {
            return false;
        }
        if (this.run != other.run) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + this.seedGroup;
        hash = 97 * hash + this.instanceId;
        hash = 97 * hash + this.run;
        return hash;
    }
}
