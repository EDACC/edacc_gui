/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.model;

/**
 *
 * @author dgall
 */
public class ExperimentHasGridQueue extends BaseModel {

    private int idExperiment;
    private int idGridQueue;

    protected ExperimentHasGridQueue(int idExperiment, int idGridQueue) {
        this.idExperiment = idExperiment;
        this.idGridQueue = idGridQueue;
        this.setNew();
    }

    public int getIdExperiment() {
        return idExperiment;
    }

    public void setIdExperiment(int idExperiment) {
        this.idExperiment = idExperiment;
    }

    public int getIdGridQueue() {
        return idGridQueue;
    }

    public void setIdGridQueue(int idGridQueue) {
        this.idGridQueue = idGridQueue;
    }
}
