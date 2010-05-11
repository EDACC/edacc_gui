/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.model;

/**
 *
 * @author simon
 */
public class ExperimentHasInstance extends BaseModel implements IntegerPKModel {
    private int id;
    private int experiment_id;
    private int instances_id;

    public int getExperiment_id() {
        return experiment_id;
    }

    public void setExperiment_id(int experiment_id) {
        this.experiment_id = experiment_id;
        if (this.isSaved()) {
            this.setModified();
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getInstances_id() {
        return instances_id;
    }

    public void setInstances_id(int instances_id) {
        this.instances_id = instances_id;
        if (this.isSaved()) {
            this.setModified();
        }
    }    
}
