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
public class GridQueue extends BaseModel {

    private int id;
    private String name;
    private String location;
    private int numNodes;
    private int numCPUs;
    private int walltime;
    private int availNodes;
    private int maxJobsQueue;
    private String description;
    private File genericPBSScript;

    public int getAvailNodes() {
        return availNodes;
    }

    public void setAvailNodes(int availNodes) {
        this.availNodes = availNodes;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    protected File getGenericPBSScript() {
        return genericPBSScript;
    }

    public void setGenericPBSScript(File genericPBSScript) {
        this.genericPBSScript = genericPBSScript;
    }

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
    }

    public int getMaxJobsQueue() {
        return maxJobsQueue;
    }

    public void setMaxJobsQueue(int maxJobsQueue) {
        this.maxJobsQueue = maxJobsQueue;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumCPUs() {
        return numCPUs;
    }

    public void setNumCPUs(int numCPUs) {
        this.numCPUs = numCPUs;
    }

    public int getNumNodes() {
        return numNodes;
    }

    public void setNumNodes(int numNodes) {
        this.numNodes = numNodes;
    }

    public int getWalltime() {
        return walltime;
    }

    public void setWalltime(int walltime) {
        this.walltime = walltime;
    }
}
