package edacc.importexport;

import edacc.model.DatabaseConnector;
import edacc.model.Experiment;
import edacc.model.ExperimentDAO;
import edacc.model.ExperimentHasInstance;
import edacc.model.Instance;
import edacc.model.InstanceDAO;
import edacc.model.Parameter;
import edacc.model.Solver;
import edacc.model.SolverBinaries;
import edacc.model.SolverConfiguration;
import edacc.model.SolverConfigurationDAO;
import edacc.model.SolverDAO;
import edacc.model.Tasks;
import edacc.model.Verifier;
import edacc.model.VerifierConfiguration;
import edacc.model.VerifierDAO;
import edacc.util.Pair;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

/**
 *
 * @author simon
 */
public class ImportController implements ImportExportController {
    
    private ZipFile zipFile;
    private List<Experiment> experiments;
    private List<Solver> solvers;
    private List<Instance> instances;
    private List<Verifier> verifiers;
    private HashMap<Integer, List<SolverConfiguration>> solverConfigs;
    
    public ImportController(File file) throws ZipException, IOException, ClassNotFoundException {
        zipFile = new ZipFile(file);
        experiments = ExperimentDAO.readExperimentsFromFile(zipFile);
        solvers = SolverDAO.readSolversFromFile(zipFile);
        instances = InstanceDAO.readInstancesFromFile(zipFile);
        verifiers = VerifierDAO.readVerifiersFromFile(zipFile);
        
        solverConfigs = new HashMap<Integer, List<SolverConfiguration>>();
        for (Experiment exp : experiments) {
            solverConfigs.put(exp.getId(), SolverConfigurationDAO.readSolverConfigurationsFromFile(zipFile, exp));
        }
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
    public List<Solver> getDependentSolvers(List<Experiment> experiments) {
        HashMap<Integer, Solver> solverMap = new HashMap<Integer, Solver>();
        for (Solver s : solvers) {
            solverMap.put(s.getId(), s);
        }
        HashSet<Integer> solverIds = new HashSet<Integer>();
        for (Experiment exp : experiments) {
            List<SolverConfiguration> scs = solverConfigs.get(exp.getId());
            for (SolverConfiguration sc : scs) {
                solverIds.add(sc.getSolverBinary().getIdSolver());
            }
        }
        List<Solver> res = new ArrayList<Solver>();
        for (Integer solverId : solverIds) {
            res.add(solverMap.get(solverId));
        }
        return res;
    }
    
    @Override
    public List<Instance> getDependentInstances(List<Experiment> experiments) {
        HashMap<Integer, Instance> instanceMap = new HashMap<Integer, Instance>();
        for (Instance i : instances) {
            instanceMap.put(i.getId(), i);
        }
        HashSet<Integer> instanceIds = new HashSet<Integer>();
        for (Experiment exp : experiments) {
            for (ExperimentHasInstance ehi : exp.instances) {
                instanceIds.add(ehi.getInstances_id());
            }
        }
        List<Instance> res = new ArrayList<Instance>();
        for (Integer instanceId : instanceIds) {
            res.add(instanceMap.get(instanceId));
        }
        return res;
    }
    
    public HashMap<Integer, List<Solver>> mapFileSolversToExistingSolversWithSolverBinariesInCommon(List<Solver> fileSolvers) throws SQLException {
        return SolverDAO.mapFileSolversToExistingSolversWithSolverBinariesInCommon(fileSolvers);
    }
    
    public HashMap<Integer, List<Solver>> mapFileSolversToExistingSolversWithSameParameters(List<Solver> fileSolvers) throws SQLException {
        return SolverDAO.mapFileSolversToExistingSolversWithSameParameters(fileSolvers);
    }
    
    public HashMap<Integer, List<Verifier>> mapFileVerifiersToExistingVerifiers(List<Verifier> fileVerifiers) throws SQLException {
        return VerifierDAO.mapFileVerifiersToExistingVerifiers(fileVerifiers);
    }
    
    public List<Solver> getDatabaseSolvers() throws SQLException {
        return SolverDAO.getAll();
    }
    
    public void importData(Tasks task, List<Experiment> selectedExperiments, List<Solver> selectedSolvers, List<Instance> selectedInstances, HashMap<Integer, Solver> solverMap, HashMap<Integer, String> nameMap) throws Exception {
        boolean autoCommit = DatabaseConnector.getInstance().getConn().getAutoCommit();
        try {
            DatabaseConnector.getInstance().getConn().setAutoCommit(false);
            HashMap<Integer, Instance> instanceMap = InstanceDAO.importInstances(task, zipFile, selectedInstances);
            Pair<HashMap<Integer, SolverBinaries>, HashMap<Integer, Parameter>> pair = SolverDAO.importSolvers(task, zipFile, selectedSolvers, solverMap, nameMap);
            ExperimentDAO.importExperiments(task, zipFile, selectedExperiments, pair.getFirst(), pair.getSecond(), instanceMap);
        } catch (Exception ex) {
            if (autoCommit) {
                DatabaseConnector.getInstance().getConn().rollback();
            }
            throw ex;
        } finally {
            DatabaseConnector.getInstance().getConn().setAutoCommit(autoCommit);
        }
    }

    @Override
    public List<Verifier> getVerifiers() {
        return verifiers;
    }

    @Override
    public List<Verifier> getDependentVerifiers(List<Experiment> experiments) throws Exception {
        List<Verifier> depVerifiers = new ArrayList<Verifier>();
        for (Experiment exp : experiments) {
            if (exp.verifierConfig != null) {
                for (Verifier v : verifiers) {
                    if (v.getId() == exp.verifierConfig.getVerifier().getId()) {
                        depVerifiers.add(v);
                        break;
                    }
                }
            }
        }
        return depVerifiers;
    }
}
