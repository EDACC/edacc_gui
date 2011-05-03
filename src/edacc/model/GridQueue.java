/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edacc.model;

import java.io.File;

/**
 *
 * @author dgall
 */
public class GridQueue extends BaseModel implements IntegerPKModel {

    private int id;
    private String name;
    private String location;
    private int numCPUs;
    private String description;
    private int numCores;
    private int numThreads;
    private boolean hyperthreading;
    private boolean turboboost;
    private String CPUName;
    private int cacheSize;
    private String cpuflags;
    private long memory;
    private String cpuinfo;
    private String meminfo;

    public GridQueue() {
        this.setNew();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        if (this.isSaved()) {
            this.setModified();
        }
    }

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
        if (this.isSaved()) {
            this.setModified();
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        if (this.isSaved()) {
            this.setModified();
        }
    }

    public int getNumCPUs() {
        return numCPUs;
    }

    public void setNumCPUs(int numCPUs) {
        this.numCPUs = numCPUs;
        if (this.isSaved()) {
            this.setModified();
        }
    }

    @Override
    public String toString() {
        return getName();
    }

    /**
     * @return the numCores
     */
    public int getNumCores() {
        return numCores;
    }

    /**
     * @param numCores the numCores to set
     */
    public void setNumCores(int numCores) {
        this.numCores = numCores;
    }

    /**
     * @return the numThreads
     */
    public int getNumThreads() {
        return numThreads;
    }

    /**
     * @param numThreads the numThreads to set
     */
    public void setNumThreads(int numThreads) {
        this.numThreads = numThreads;
    }

    /**
     * @return the hyperthreading
     */
    public boolean isHyperthreading() {
        return hyperthreading;
    }

    /**
     * @param hyperthreading the hyperthreading to set
     */
    public void setHyperthreading(boolean hyperthreading) {
        this.hyperthreading = hyperthreading;
    }

    /**
     * @return the turboboost
     */
    public boolean isTurboboost() {
        return turboboost;
    }

    /**
     * @param turboboost the turboboost to set
     */
    public void setTurboboost(boolean turboboost) {
        this.turboboost = turboboost;
    }

    /**
     * @return the CPUName
     */
    public String getCPUName() {
        return CPUName;
    }

    /**
     * @param CPUName the CPUName to set
     */
    public void setCPUName(String CPUName) {
        this.CPUName = CPUName;
    }

    /**
     * @return the cacheSize
     */
    public int getCacheSize() {
        return cacheSize;
    }

    /**
     * @param cacheSize the cacheSize to set
     */
    public void setCacheSize(int cacheSize) {
        this.cacheSize = cacheSize;
    }

    /**
     * @return the cpuflags
     */
    public String getCpuflags() {
        return cpuflags;
    }

    /**
     * @param cpuflags the cpuflags to set
     */
    public void setCpuflags(String cpuflags) {
        this.cpuflags = cpuflags;
    }

    /**
     * @return the memory
     */
    public long getMemory() {
        return memory;
    }

    /**
     * @param memory the memory to set
     */
    public void setMemory(long memory) {
        this.memory = memory;
    }

    /**
     * @return the cpuinfo
     */
    public String getCpuinfo() {
        return cpuinfo;
    }

    /**
     * @param cpuinfo the cpuinfo to set
     */
    public void setCpuinfo(String cpuinfo) {
        this.cpuinfo = cpuinfo;
    }

    /**
     * @return the meminfo
     */
    public String getMeminfo() {
        return meminfo;
    }

    /**
     * @param meminfo the meminfo to set
     */
    public void setMeminfo(String meminfo) {
        this.meminfo = meminfo;
    }
}
