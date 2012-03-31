package edacc.importexport;

import edacc.model.DatabaseConnector;
import edacc.model.Experiment;
import edacc.model.ExperimentDAO;
import edacc.model.ExperimentHasInstance;
import edacc.model.ExperimentHasInstanceDAO;
import edacc.model.Instance;
import edacc.model.InstanceClassMustBeSourceException;
import edacc.model.InstanceDAO;
import edacc.model.InstanceNotInDBException;
import edacc.model.NoConnectionToDBException;
import edacc.model.Solver;
import edacc.model.SolverConfiguration;
import edacc.model.SolverConfigurationDAO;
import edacc.model.SolverDAO;
import edacc.model.Tasks;
import edacc.model.Verifier;
import edacc.model.VerifierConfiguration;
import edacc.model.VerifierConfigurationDAO;
import edacc.model.VerifierDAO;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.zip.ZipOutputStream;
import javax.xml.bind.JAXBException;

/**
 *
 * @author simon
 */
public class ExportController implements ImportExportController {

    private List<Experiment> experiments;
    private List<Solver> solvers;
    private List<Instance> instances;
    private List<Verifier> verifiers;

    public ExportController() throws SQLException, InstanceClassMustBeSourceException, IOException {
        ExperimentDAO.clearCache();
        SolverDAO.clearCache();
        InstanceDAO.clearCache();
        VerifierDAO.clearCache();
        experiments = ExperimentDAO.getAll();
        solvers = SolverDAO.getAll();
        instances = InstanceDAO.getAll();
        verifiers = VerifierDAO.getAllVerifiers();
    }

    @Override
    public List<Experiment> getExperiments() {
        return experiments;
    }

    @Override
    public List<Solver> getSolvers() {
        return solvers;
    }

    @Override
    public List<Instance> getInstances() {
        return instances;
    }

    @Override
    public List<Verifier> getVerifiers() {
        return verifiers;
    }

    @Override
    public List<Solver> getDependentSolvers(List<Experiment> experiments) throws Exception {
        HashSet<Integer> solverIds = new HashSet<Integer>();
        for (Experiment exp : experiments) {
            for (SolverConfiguration sc : SolverConfigurationDAO.getSolverConfigurationByExperimentId(exp.getId())) {
                solverIds.add(sc.getSolverBinary().getIdSolver());
            }
        }
        List<Solver> depSolvers = new ArrayList<Solver>();
        for (Integer solverId : solverIds) {
            depSolvers.add(SolverDAO.getById(solverId));
        }
        return depSolvers;
    }

    @Override
    public List<Instance> getDependentInstances(List<Experiment> experiments) throws Exception {
        HashSet<Integer> instanceIds = new HashSet<Integer>();
        for (Experiment exp : experiments) {
            for (ExperimentHasInstance ehi : ExperimentHasInstanceDAO.getExperimentHasInstanceByExperimentId(exp.getId())) {
                instanceIds.add(ehi.getInstances_id());
            }
        }
        List<Instance> depInstances = new ArrayList<Instance>();
        for (Integer instanceId : instanceIds) {
            depInstances.add(InstanceDAO.getById(instanceId));
        }
        return depInstances;
    }

    public void export(Tasks task, File file, List<Experiment> experiments, List<Solver> solvers, List<Instance> instances, List<Verifier> verifiers) throws FileNotFoundException, IOException, SQLException, JAXBException, NoConnectionToDBException, InstanceNotInDBException, InterruptedException {
        boolean autoCommit = DatabaseConnector.getInstance().getConn().getAutoCommit();
        try {
            DatabaseConnector.getInstance().getConn().setAutoCommit(false);
            ZipOutputStream os = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
            SolverDAO.exportSolvers(task, os, solvers);
            InstanceDAO.exportInstances(task, os, instances);
            ExperimentDAO.exportExperiments(task, os, experiments);
            VerifierDAO.exportVerifiers(task, os, verifiers);
            os.close();
        } finally {
            DatabaseConnector.getInstance().getConn().setAutoCommit(autoCommit);
        }
    }

    @Override
    public List<Verifier> getDependentVerifiers(List<Experiment> experiments) throws Exception {
        HashSet<Integer> expIds = new HashSet<Integer>();
        List<Verifier> res = new ArrayList<Verifier>();
        for (Experiment exp : experiments) {
            expIds.add(exp.getId());
        }
        for (VerifierConfiguration vc : VerifierConfigurationDAO.getAll()) {
            if (expIds.contains(vc.getIdExperiment())) {
                res.add(vc.getVerifier());
            }
        }
        return res;
    }
}
