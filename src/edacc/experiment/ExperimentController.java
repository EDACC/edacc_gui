package edacc.experiment;

import edacc.model.Experiment;
import edacc.model.ExperimentDAO;
import edacc.model.Instance;
import edacc.model.InstanceDAO;
import edacc.EDACCView;
import java.sql.SQLException;
import java.util.Vector;


/**
 *
 * @author daniel
 */
public class ExperimentController {
    EDACCView view;
    private Experiment activeExperiment;
    private Vector<Experiment> experiments;
    private Vector<Instance> instances;

    public ExperimentController(EDACCView view) {
        this.view = view;
    }

    public void initialize() {
        try {
            Vector<Experiment> v = new Vector<Experiment>();
            v.addAll(ExperimentDAO.getAll());
            experiments = v;
            view.expTableModel.setExperiments(experiments);

            Vector<Instance> vi = new Vector<Instance>();
            vi.addAll(InstanceDAO.getAll());
            instances = vi;
            System.out.println(instances.size());
            view.insTableModel.setInstances(instances);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadExperiment(int id) {
        try {
            activeExperiment = ExperimentDAO.getById(id);

        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
