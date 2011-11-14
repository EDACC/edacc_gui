package edacc.model;

import java.sql.SQLException;
import java.util.ArrayList;

public class SolverConfiguration extends BaseModel implements IntegerPKModel {

    private SolverBinaries solverBinary;
    private int experiment_id;
    private int id;
    private int seed_group;
    private String name;
    private Float cost;
    private String cost_function;
    private String parameter_hash;
    private String hint;

    public Float getCost() {
        return cost;
    }

    public void setCost(Float cost) {
        if (this.isSaved() && (cost == null && this.cost != null || !cost.equals(this.cost))) {
            this.setModified();
        }
        this.cost = cost;
    }

    public String getCost_function() {
        return cost_function;
    }

    public void setCost_function(String cost_function) {
        if (this.isSaved() && (cost_function == null && this.cost_function != null || !cost_function.equals(this.cost_function))) {
            this.setModified();
        }
        this.cost_function = cost_function;

    }

    public String getParameter_hash() {
        return parameter_hash;
    }

    public void setParameter_hash(String parameter_hash) {
        this.parameter_hash = parameter_hash;
    }

    public int getSeed_group() {
        return seed_group;
    }

    public void setSeed_group(int seed_group) {
        if (this.seed_group != seed_group && this.isSaved()) {
            this.setModified();
        }
        this.seed_group = seed_group;
    }

    public int getExperiment_id() {
        return experiment_id;
    }

    public void setExperiment_id(int experiment_id) {
        this.experiment_id = experiment_id;
        if (this.isSaved()) {
            this.setModified();
        }
    }

    @Override
    public int getId() {
        return id;
    }

    protected void setId(int id) {
        this.id = id;
    }

    public SolverBinaries getSolverBinary() {
        return solverBinary;
    }

    public void setSolverBinary(SolverBinaries solverBinary) {
        if (this.isSaved() && this.getSolverBinary() != solverBinary) {
            this.setModified();
        }
        this.solverBinary = solverBinary;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (!name.equals(this.name) && this.isSaved()) {
            this.setModified();
        }
        this.name = name;
    }

    public void setHint(String hint) {
        if (!hint.equals(this.hint) && this.isSaved()) {
            this.setModified();
        }
        this.hint = hint;
    }

    public String getHint() {
        return hint;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SolverConfiguration other = (SolverConfiguration) obj;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + this.id;
        return hash;
    }

    public boolean hasEqualSemantics(SolverConfiguration sc) throws SQLException {
        boolean equal = true;
        if (sc.getSolverBinary().getId() != getSolverBinary().getId()) {
            // if the solver configs doesn't have the same solver binary -> other semantics
            equal = false;
        } else {
            ArrayList<ParameterInstance> paramInstances = ParameterInstanceDAO.getBySolverConfig(sc);
            ArrayList<ParameterInstance> myParamInstances = ParameterInstanceDAO.getBySolverConfig(this);
            if (paramInstances.size() != myParamInstances.size()) {
                // if number of parameters doesn't equal, the solver config has other semantics
                equal = false;
            } else {
                // try to find every parameter instance
                // if every parameter instance was found with same value -> same semantics
                for (ParameterInstance hisPi : myParamInstances) {
                    boolean found = false;
                    for (ParameterInstance myPi : paramInstances) {
                        if (myPi.getParameter_id() == hisPi.getParameter_id()
                                && (hisPi.getValue() == null && myPi.getValue() == null || myPi.getValue().equals(hisPi.getValue()))) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        equal = false;
                        break;
                    }
                }
            }
        }
        return equal;
    }

    public int getSemanticsHashCode() throws SQLException {
        int semanticsHash = 3;
        semanticsHash = 97 * semanticsHash + solverBinary.getId();
        ArrayList<ParameterInstance> myParamInstances = ParameterInstanceDAO.getBySolverConfig(this);
        semanticsHash = 97 * semanticsHash + myParamInstances.size();
        for (ParameterInstance myPi : myParamInstances) {
            semanticsHash = 97 * semanticsHash + myPi.getParameter_id();
            if (myPi.getValue() == null) {
                semanticsHash = 97 * semanticsHash + 3;
            } else {
                semanticsHash = 97 * semanticsHash + myPi.getValue().hashCode();
            }
        }
        return semanticsHash;
    }
}
