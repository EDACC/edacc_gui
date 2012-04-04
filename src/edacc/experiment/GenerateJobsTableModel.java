package edacc.experiment;

import edacc.model.Instance;
import edacc.model.InstanceClassMustBeSourceException;
import edacc.model.InstanceDAO;
import edacc.model.SolverConfiguration;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * The generate jobs table model
 * @author simon
 */
public class GenerateJobsTableModel extends ThreadSafeDefaultTableModel {

    private ArrayList<SolverConfiguration> solverConfigs;
    private ArrayList<Instance> instances;
    private HashMap<Integer, Integer> rowMap;
    private HashMap<Integer, Integer> colMap;
    private int[][] numRuns;
    private int[][] savedNumRuns;
    private ExperimentController expController;
    
    /**
     * Creates a new generate jobs table model.
     * @param expController the experiment controller to be used
     */
    public GenerateJobsTableModel(ExperimentController expController) {
        this.expController = expController;
    }
    
    /**
     * Updates the number of runs by using the experiment result cache of the experiment controller.<br/>
     * The cache should be updated first to get valid results.
     * @throws SQLException
     * @throws InstanceClassMustBeSourceException
     * @throws IOException 
     */
    public void updateNumRuns() throws SQLException, InstanceClassMustBeSourceException, IOException {
        solverConfigs = expController.getSolverConfigurations();
        instances = new ArrayList<Instance>();
        instances.addAll(InstanceDAO.getAllByExperimentId(expController.getActiveExperiment().getId()));
        numRuns = new int[instances.size()][solverConfigs.size()+1];
        savedNumRuns = new int[instances.size()][solverConfigs.size()+1];
        rowMap = new HashMap<Integer, Integer>();
        colMap = new HashMap<Integer, Integer>();
        for (int row = 0; row < instances.size(); row++) {
            rowMap.put(instances.get(row).getId(), row);
        }
        for (int col = 1; col <= solverConfigs.size(); col++) {
            colMap.put(solverConfigs.get(col-1).getId(), col);
        }
        for (SolverConfiguration sc : solverConfigs) {
            for (Instance i : instances) {
                int numRuns = expController.getExperimentResults().getNumRuns(sc.getId(), i.getId());
                setNumRuns(i, sc, numRuns);
                setSavedNumRuns(i, sc, numRuns);
            }
        }
        this.fireTableStructureChanged();
    }


    @Override
    public int getColumnCount() {
        return solverConfigs==null?0:solverConfigs.size() +1;
    }

    @Override
    public String getColumnName(int column) {
        if (column == 0) {
            return "";
        } else {
            return solverConfigs.get(column-1).getName();
        }
    }

    @Override
    public int getRowCount() {
        return instances==null?0:instances.size();
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (column == 0) {
            return instances.get(row).getName();
        } else {
            return numRuns[row][column];
        }
    }

    @Override
    public void setValueAt(Object aValue, int row, int column) {
        numRuns[row][column] = (Integer)aValue;
        this.fireTableCellUpdated(row, column);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return column >= 1;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (this.getRowCount() == 0) {
            return String.class;
        } else {
            return getValueAt(0,columnIndex).getClass();
        }
    }

    /**
     * Sets the number of runs for a (instance, solver configuration)-pair.
     * @param instance the instance
     * @param solverConfig the solver configuration
     * @param value the number of runs 
     */
    public void setNumRuns(Instance instance, SolverConfiguration solverConfig, Integer value) {
        numRuns[rowMap.get(instance.getId())][colMap.get(solverConfig.getId())] = value;
    }

    /**
     * Returns the number of runs for a (instance, solver configuration)-pair.
     * @param instance the instance
     * @param solverConfig the solver configuration
     * @return the number of runs
     */
    public Integer getNumRuns(Instance instance, SolverConfiguration solverConfig) {
        return numRuns[rowMap.get(instance.getId())][colMap.get(solverConfig.getId())];
    }

    /**
     * Returns the instance represented by the row
     * @param row the row for which an instance should be returned
     * @return the instance
     */
    public Instance getInstance(int row) {
        return instances.get(row);
    }

    /**
     * Returns the solver configuration represented by the column
     * @param col the column for which a solver configuration should be returned
     * @return the solver configuration
     */
    public SolverConfiguration getSolverConfiguration(int col) {
        return solverConfigs.get(col-1);
    }

    /**
     * Sets the saved number of runs for a (instance, solver configuration)-pair.
     * @param instance the instance
     * @param solverConfig the solver configuration
     * @param value the saved number of runs 
     */
    public void setSavedNumRuns(Instance instance, SolverConfiguration solverConfig, int value) {
        savedNumRuns[rowMap.get(instance.getId())][colMap.get(solverConfig.getId())] = value;
    }
    
    /**
     * Returns the saved number of runs for a (instance, solver configuration)-pair.
     * @param instance the instance
     * @param solverConfig the solver configuration
     * @return the saved number of runs
     */
    public Integer getSavedNumRuns(Instance instance, SolverConfiguration solverConfig) {
        return savedNumRuns[rowMap.get(instance.getId())][colMap.get(solverConfig.getId())];
    }
}
