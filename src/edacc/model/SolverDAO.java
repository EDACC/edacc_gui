package edacc.model;

import edacc.manageDB.NoSolverBinarySpecifiedException;
import edacc.manageDB.NoSolverNameSpecifiedException;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.LinkedList;
import edacc.manageDB.Util;
import edacc.util.Pair;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import javax.xml.bind.JAXBException;

/**
 *
 * @author simon
 */
public class SolverDAO {

    protected static final String table = "Solver";
    protected static final String insertQuery = "INSERT INTO " + table + " (`name`, `description`, `code`, `authors`, `version`) VALUES (?, ?, ?, ?, ?)";
    protected static final String updateQueryCode = "UPDATE " + table + " SET `code`=? WHERE `idSolver`=?";
    protected static final String updateQuery = "UPDATE " + table + " SET `name`=?, `description`=?, `authors`=?, `version`=? WHERE `idSolver`=?";
    protected static final String removeQuery = "DELETE FROM " + table + " WHERE idSolver=?";
    private static final ObjectCache<Solver> cache = new ObjectCache<Solver>();

    /**
     * persists a solver to database and assigns an id. it also ensures that
     * the solver is cached.
     * @param solver The Solver object to persist.
     */
    public static void save(Solver solver) throws SQLException, FileNotFoundException, NoSolverBinarySpecifiedException, NoSolverNameSpecifiedException, IOException, NoSuchAlgorithmException {
        save(solver, false);
    }

    public static void save(Solver solver, boolean allowNoSolverBinary) throws SQLException, FileNotFoundException, NoSolverBinarySpecifiedException, NoSolverNameSpecifiedException, IOException, NoSuchAlgorithmException {
        if (solver == null) {
            return;
        }
        if (solver.isSaved()) {
            for (SolverBinaries sb : solver.getSolverBinaries()) {
                if (sb.isModified()) {
                    solver.setModified();
                    break;
                }
            }
            for (CostBinary cb : solver.getCostBinaries()) {
                if (cb.isModified()) {
                    solver.setModified();
                    break;
                }
            }
        }
        if (solver.isSaved()) {
            return;
        }
        // new solvers without binary aren't allowed
        if (solver.isNew() && solver.getName().isEmpty()) {
            throw new NoSolverNameSpecifiedException();
        }
        if (solver.isNew() && !allowNoSolverBinary && solver.getSolverBinaries().size() == 0) {
            throw new NoSolverBinarySpecifiedException();
        }

        PreparedStatement ps;

        // check for duplicates by MD5 sum
        //boolean alreadyInDB = solverAlreadyInDB(solver) != null;

        // insert  into db
        if (solver.isNew()) {// && !alreadyInDB) {
            ps = DatabaseConnector.getInstance().getConn().prepareStatement(insertQuery, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setString(1, solver.getName());
            ps.setString(2, solver.getDescription());
            if (solver.getCodeFile() != null && solver.getCodeFile().length > 0) {
                // zip up directory
                ByteArrayOutputStream zipped = Util.zipFileArrayToByteStreamAutoBasePath(solver.getCodeFile());
                ps.setBinaryStream(3, new ByteArrayInputStream(zipped.toByteArray()));
            } else {
                ps.setNull(3, Types.BLOB);
            }
            ps.setString(4, solver.getAuthors());
            ps.setString(5, solver.getVersion());
            ps.executeUpdate();
        } else {
            // if solver already in DB, then update it
            // first update the basic data
            ps = DatabaseConnector.getInstance().getConn().prepareStatement(updateQuery);
            ps.setString(1, solver.getName());
            ps.setString(2, solver.getDescription());
            ps.setString(3, solver.getAuthors());
            ps.setString(4, solver.getVersion());
            ps.setInt(5, solver.getId());
            ps.executeUpdate();

            // update the code if necessary
            if (solver.getCodeFile() != null) { // if code is null, don't update the code (at the moment code can't be deleted)
                ps = DatabaseConnector.getInstance().getConn().prepareStatement(updateQueryCode);
                // zip up directory
                ByteArrayOutputStream zipped = Util.zipFileArrayToByteStreamAutoBasePath(solver.getCodeFile());
                ps.setBinaryStream(1, new ByteArrayInputStream(zipped.toByteArray()));
                ps.setInt(2, solver.getId());
                ps.executeUpdate();
            }
        }

        // set id if solver is new (id is assigned by the db automatically)
        if (solver.isNew()) {// && !alreadyInDB) {
            // set id
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                solver.setId(rs.getInt(1));
            }
        }/* else if (alreadyInDB) {
        solver.setId(solverAlreadyInDB(solver).getId());
        }*/

        // save SolverBinaries
        for (SolverBinaries b : solver.getSolverBinaries()) {
            // set new id of solver for the binary
            b.setIdSolver(solver.getId());
            SolverBinariesDAO.save(b);
        }

        for (CostBinary b : solver.getCostBinaries()) {
            b.setIdSolver(solver.getId());
            CostDAO.saveBinary(b);
        }

        cache.cache(solver);
        solver.setSaved();
    }

    /**
     * Removes a solver from DB and cache. It also ensures that all parameters of a solver are deleted.
     * @param solver the solver to remove.
     * @throws SQLException if an error occurs while executing the SQL query.
     * @throws SolverIsInExperimentException if the solver is used in an experiment. In this case you have to remove the experiment first.
     * @throws SolverNotInDBException if the solver is not persisted in the db. In this case, the object will be marked as "deleted" but nothing will be done to the cache or db.
     */
    public static void removeSolver(Solver solver) throws SolverIsInExperimentException, SQLException, SolverNotInDBException, NoSolverBinarySpecifiedException, FileNotFoundException, IOException {
        if (solver.isNew()) {
            solver.setDeleted();
            throw new SolverNotInDBException(solver);
        }

        // don't remove solver if it is used in an experiment
        if (isInExperiment(solver)) {
            throw new SolverIsInExperimentException(solver);
        }

        // remove also the parameters of the solver
        // not necessary because the db model deletes them
        //ParameterDAO.removeParametersOfSolver(solver);

        // remove also the solver binaries of the solver
        // not necessary because the db model deletes them
        //SolverBinariesDAO.removeBinariesOfSolver(solver);

        // now remove the solver from the db
        PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement(removeQuery);
        ps.setInt(1, solver.getId());
        ps.executeUpdate();

        // finally remove it from the cache if cached
        cache.remove(solver);

        // mark the object as deleted
        solver.setDeleted();
    }

    private static Solver getSolverFromResultset(ResultSet rs) throws SQLException {
        Solver i = new Solver();
        i.setId(rs.getInt("idSolver"));
        i.setName(rs.getString("name"));
        i.setDescription(rs.getString("description"));
        i.setAuthor(rs.getString("authors"));
        i.setVersion(rs.getString("version"));
        i.setSolverBinaries(SolverBinariesDAO.getBinariesOfSolver(i));
        i.setCostBinaries(CostDAO.getCostBinariesForSolver(i));
        return i;
    }

    /**
     * retrieves an solver from the database
     * @param id the id of the solver to be retrieved
     * @return the solver specified by its id
     * @throws SQLException
     */
    public static Solver getById(int id) throws SQLException {
        Solver c = cache.getCached(id);
        if (c != null) {
            return c;
        }

        PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement("SELECT * FROM " + table + " WHERE idSolver=?");
        st.setInt(1, id);
        ResultSet rs = st.executeQuery();
        if (rs.next()) {
            Solver i = getSolverFromResultset(rs);

            i.setSaved();
            cache.cache(i);
            return i;
        }
        return null;
    }

    /**
     * retrieves all solvers from the database
     * @return all solvers in a List
     * @throws SQLException
     */
    public static LinkedList<Solver> getAll() throws SQLException {
        Statement st = DatabaseConnector.getInstance().getConn().createStatement();
        ResultSet rs = st.executeQuery("SELECT idSolver, name, description, authors, version FROM " + table);
        LinkedList<Solver> res = new LinkedList<Solver>();
        while (rs.next()) {
            Solver c = cache.getCached(rs.getInt("idSolver"));
            if (c != null) {
                res.add(c);
            } else {
                Solver i = getSolverFromResultset(rs);
                i.setSaved();
                cache.cache(i);
                res.add(i);
            }

        }
        rs.close();
        return res;
    }

    /**
     * Tests if a solver is already in DB (by MD5) and returns the cached object or
     * null if solver isn't in DB.
     * @param s
     * @return
     * @throws NoConnectionToDBException
     * @throws SQLException
     */
    /* public static Solver solverAlreadyInDB(Solver s) throws NoConnectionToDBException, SQLException {
    PreparedStatement ps;
    final String Query = "SELECT idSolver, name, binaryName, description, md5, authors, version FROM " + table + " WHERE md5 = ?";
    ps = DatabaseConnector.getInstance().getConn().prepareStatement(Query);
    ps.setString(1, s.getMd5());
    ResultSet rs = ps.executeQuery();
    
    if (rs.next()) {
    Solver c = cache.getCached(rs.getInt("idSolver"));
    if (c != null) {
    return c;
    }
    else {
    Solver i = getSolverFromResultset(rs);
    i.setSaved();
    cache.cache(i);
    return i;
    }
    }
    return null;
    }*/
    private static boolean isInExperiment(Solver solver) throws NoConnectionToDBException, SQLException {
        Statement st = DatabaseConnector.getInstance().getConn().createStatement();

        ResultSet rs = st.executeQuery("SELECT s.idSolver FROM " + table + " AS s "
                + "JOIN SolverConfig as sc "
                + "JOIN SolverBinaries AS sb "
                + "ON s.idSolver = sb.idSolver "
                + "AND sc.SolverBinaries_idSolverBinary = sb.idSolverBinary "
                + "WHERE s.idSolver = " + solver.getId());
        return rs.next();
    }

    /**
     * Copies the binary file of a solver to a specified location on the file system.
     * @param s
     * @param f the file where the binary shall be stored
     * @return
     */
    /*  public static void getBinaryFileOfSolver(Solver s, File f) throws NoConnectionToDBException, SQLException, FileNotFoundException, IOException {
    PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement("SELECT `binary` FROM " + table + " WHERE idSolver=?");
    ps.setInt(1, s.getId());
    ResultSet rs = ps.executeQuery();
    if (rs.next()) {
    FileOutputStream out = new FileOutputStream(f);
    InputStream in = rs.getBinaryStream("binary");
    int len = 0;
    byte[] buffer = new byte[256*1024];
    while ((len = in.read(buffer)) > -1) {
    out.write(buffer, 0, len);
    }
    out.close();
    in.close();
    }
    }*/
    /**
     * Copies the binary file of a solver to a temporary location on the file system
     * and returns a File reference on it.
     * @param s
     * @return
     * @throws NoConnectionToDBException
     * @throws SQLException
     * @throws FileNotFoundException
     * @throws IOException
     */
    /*  public static File getBinaryFileOfSolver(Solver s) throws NoConnectionToDBException, SQLException, FileNotFoundException, IOException {
    File f = new File("tmp" + System.getProperty("file.separator") + s.getBinaryName());
    // create missing direcotries
    f.getParentFile().mkdirs();
    getBinaryFileOfSolver(s, f);
    return f;
    }*/
    /**
     * Exports the code of the solver s to the directory specified by f
     * @param s solver
     * @param f File referencing a directory on the filesystem
     * @throws NoConnectionToDBException
     * @throws SQLException
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void exportSolverCode(Solver s, File f) throws NoConnectionToDBException, SQLException, FileNotFoundException, IOException {
        PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement("SELECT `code` FROM " + table + " WHERE idSolver=?");
        ps.setInt(1, s.getId());
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            InputStream in = rs.getBinaryStream("code");
            if (in == null) {
                return;
            }

            // open temporary file to write the zip file to
            new File("tmp").mkdir();
            File tmp = new File("tmp" + System.getProperty("file.separator") + s.getId() + ".zip.tmp");
            FileOutputStream out = new FileOutputStream(tmp);


            byte[] buffer = new byte[8192];
            int read;
            while (-1 != (read = in.read(buffer))) {
                out.write(buffer, 0, read);
            }
            out.close();
            in.close();

            Util.unzip(tmp, f);
            tmp.delete(); // delete temporary file
        }
        rs.close();
    }

    public static void clearCache() {
        cache.clear();
    }

    /**
     * Returns the competition categories of the solver as list of strings.
     * @param solver
     * @return
     */
    public static ArrayList<String> getCompetitionCategories(Solver solver) throws NoConnectionToDBException, SQLException {
        ArrayList<String> res = new ArrayList<String>();
        String query = "SELECT CompetitionCategory.name as name FROM CompetitionCategory LEFT JOIN Solver_has_CompetitionCategory "
                + "ON idCompetitionCategory = CompetitionCategory_idCompetitionCategory "
                + "WHERE Solver_idSolver=?;";
        PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement(query);
        ps.setInt(1, solver.getId());
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            res.add(rs.getString("name"));
        }
        rs.close();
        ps.close();
        return res;
    }

    public static HashMap<Integer, ArrayList<String>> getCompetitionCategories() throws NoConnectionToDBException, SQLException {
        HashMap<Integer, ArrayList<String>> res = new HashMap<Integer, ArrayList<String>>();
        String query = "SELECT Solver_idSolver, CompetitionCategory.name as name FROM CompetitionCategory LEFT JOIN Solver_has_CompetitionCategory "
                + "ON idCompetitionCategory = CompetitionCategory_idCompetitionCategory";
        PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement(query);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            String name = rs.getString("name");
            Integer id = new Integer(rs.getInt("Solver_idSolver"));
            if (!res.containsKey(id)) {
                ArrayList<String> lst = new ArrayList<String>();
                lst.add(name);
                res.put(id, lst);
            } else {
                res.get(id).add(name);
            }

        }
        return res;
    }

    public static void exportSolvers(Tasks task, final ZipOutputStream stream, List<Solver> solvers) throws IOException, SQLException, JAXBException {
        task.setOperationName("Exporting solvers..");
        int current = 1;
        for (Solver s : solvers) {
            task.setStatus("Writing solver binaries of solver " + current + " / " + solvers.size());
            task.setTaskProgress(current / (float) solvers.size());
            s.parameters = ParameterDAO.getParameterFromSolverId(s.getId());
            s.graph = ParameterGraphDAO.loadParameterGraph(s);
            stream.putNextEntry(new ZipEntry("solver_" + s.getId() + ".solverbinaries"));
            SolverBinariesDAO.writeSolverBinariesToStream(new ObjectOutputStream(stream), s.getSolverBinaries());
            task.setStatus("Writing cost binaries of solver " + current + " / " + solvers.size());
            stream.putNextEntry(new ZipEntry("solver_" + s.getId() + ".costbinaries"));
            CostDAO.writeCostBinariesToStream(new ObjectOutputStream(stream), s.getCostBinaries());

            //List<Parameter> parameters = ParameterDAO.getParameterFromSolverId(s.getId());
            //stream.putNextEntry(new ZipEntry("solver_" + s.getId() + ".parameters"));
            //ParameterDAO.writeParametersToStream(new ObjectOutputStream(stream), parameters);
            current++;
        }
        task.setTaskProgress(0.f);
        task.setStatus("Writing solver informations..");
        stream.putNextEntry(new ZipEntry("solvers.edacc"));
        writeSolversToStream(new ObjectOutputStream(stream), solvers);

        for (Solver s : solvers) {
            s.parameters = null;
            s.graph = null;
        }
        task.setStatus("Done.");
    }

    public static class ImportSolversResult {

        public HashMap<Integer, SolverBinaries> mapSolverBinaries;
        public HashMap<Integer, Parameter> mapParameters;
        public HashMap<Integer, Cost> mapCosts;

        public ImportSolversResult(HashMap<Integer, SolverBinaries> mapSolverBinaries, HashMap<Integer, Parameter> mapParameters, HashMap<Integer, Cost> mapCosts) {
            this.mapSolverBinaries = mapSolverBinaries;
            this.mapParameters = mapParameters;
            this.mapCosts = mapCosts;
        }
    }

    public static ImportSolversResult importSolvers(Tasks task, final ZipFile file, List<Solver> solvers, HashMap<Integer, Solver> solverMap, HashMap<Integer, String> nameMap) throws IOException, ClassNotFoundException, SQLException, FileNotFoundException, NoSolverBinarySpecifiedException, NoSolverNameSpecifiedException, NoSuchAlgorithmException, NoConnectionToDBException, JAXBException {
        task.setOperationName("Importing solvers..");
        clearCache();

        HashMap<Integer, SolverBinaries> mapSolverBinaries = new HashMap<Integer, SolverBinaries>();
        HashMap<Integer, Parameter> mapParameters = new HashMap<Integer, Parameter>();
        HashMap<Integer, Integer> mapSolvers = new HashMap<Integer, Integer>();
        HashMap<Integer, Cost> mapCosts = new HashMap<Integer, Cost>();
        int current = 1;
        for (Solver s : solvers) {
            task.setStatus("Saving solver " + current + " / " + solvers.size());
            task.setTaskProgress(current / (float) solvers.size());
            if (solverMap.containsKey(s.getId())) {
                Solver dbSolver = solverMap.get(s.getId());
                mapSolvers.put(s.getId(), dbSolver.getId());
                for (Parameter fileParam : s.parameters) {
                    for (Parameter dbParam : ParameterDAO.getParameterFromSolverId(dbSolver.getId())) {

                        if (fileParam.realEquals(dbParam)) {
                            mapParameters.put(fileParam.getId(), dbParam);
                            break;
                        }
                    }
                    // TODO: if a param is not found we could have problems importing solver configs!
                }
                mapSolvers.put(s.getId(), dbSolver.getId());
            } else {
                Solver newDBSolver = new Solver(s);
                newDBSolver.setName(nameMap.get(s.getId()));
                SolverDAO.save(newDBSolver, true);
                if (s.graph != null) {
                    ParameterGraphDAO.saveParameterGraph(s.graph, newDBSolver);
                }

                for (Parameter p : s.parameters) {
                    Parameter newParam = new Parameter(p);
                    newParam.setIdSolver(newDBSolver.getId());
                    ParameterDAO.saveParameterForSolver(newDBSolver, newParam);
                    mapParameters.put(p.getId(), newParam);
                }

                mapSolvers.put(s.getId(), newDBSolver.getId());
            }
            List<SolverBinaries> dbSolverBinaries = SolverBinariesDAO.getBinariesOfSolver(SolverDAO.getById(mapSolvers.get(s.getId())));
            HashMap<Integer, SolverBinaries> solverBinaryMap = new HashMap<Integer, SolverBinaries>();
            for (SolverBinaries sb : s.getSolverBinaries()) {
                boolean found = false;
                for (SolverBinaries dbSb : dbSolverBinaries) {
                    if (dbSb.getMd5().equals(sb.getMd5())) {
                        found = true;
                        mapSolverBinaries.put(sb.getId(), dbSb);
                        break;
                    }
                }
                if (!found) {
                    solverBinaryMap.put(sb.getId(), sb);
                }
            }

            if (!solverBinaryMap.isEmpty()) {
                ZipEntry entry = file.getEntry("solver_" + s.getId() + ".solverbinaries");
                ObjectInputStream ois = new ObjectInputStream(file.getInputStream(entry));
                Pair<Integer, BinaryData> sbData;
                while ((sbData = SolverBinariesDAO.readSolverBinaryDataFromStream(ois)) != null) {
                    SolverBinaries sb = solverBinaryMap.get(sbData.getFirst());
                    if (sb != null) {
                        SolverBinaries dbSolverBinary = new SolverBinaries(sb);
                        dbSolverBinary.setIdSolver(mapSolvers.get(s.getId()));
                        SolverBinariesDAO.save(dbSolverBinary, sbData.getSecond());
                        mapSolverBinaries.put(sb.getId(), dbSolverBinary);
                    }
                }
            }

            List<CostBinary> dbCostBinaries = CostDAO.getCostBinariesForSolver(SolverDAO.getById(mapSolvers.get(s.getId())));
            HashMap<Integer, CostBinary> costBinaryMap = new HashMap<Integer, CostBinary>();
            for (CostBinary cb : s.getCostBinaries()) {
                boolean found = false;
                for (CostBinary dbCb : dbCostBinaries) {
                    if (dbCb.getCost().getName().equals(cb.getCost().getName())) {
                        found = true;
                        mapCosts.put(cb.getCost().getId(), dbCb.getCost());
                    }
                }
                if (!found) {
                    costBinaryMap.put(cb.getId(), cb);
                }
            }
            if (!costBinaryMap.isEmpty()) {
                ZipEntry entry = file.getEntry("solver_" + s.getId() + ".costbinaries");
                ObjectInputStream ois = new ObjectInputStream(file.getInputStream(entry));
                Pair<Integer, BinaryData> cbData;
                while ((cbData = CostDAO.readCostBinaryDataFromStream(ois)) != null) {
                    CostBinary cb = costBinaryMap.get(cbData.getFirst());
                    if (cb != null) {
                        CostBinary dbCostBinary = new CostBinary(cb);
                        dbCostBinary.data = cbData.getSecond();
                        dbCostBinary.setIdSolver(mapSolvers.get(s.getId()));
                        dbCostBinary.setNew();
                        Cost c = CostDAO.getCostByName(dbCostBinary.getCost().getName());
                        if (c == null) {
                            c = new Cost(dbCostBinary.getCost());
                            c.setNew();
                            CostDAO.saveCost(c);
                            dbCostBinary.setCost(c);

                        }
                        mapCosts.put(cb.getCost().getId(), c);
                        CostDAO.saveBinary(dbCostBinary);
                        dbCostBinary.data = null;
                    }
                }
            }
            current++;
        }
        task.setStatus("Done.");
        task.setTaskProgress(0.f);
        clearCache();
        return new ImportSolversResult(mapSolverBinaries, mapParameters, mapCosts);
    }

    public static void writeSolversToStream(ObjectOutputStream stream, List<Solver> solvers) throws IOException, SQLException {
        for (Solver s : solvers) {
            stream.writeUnshared(s);
        }
    }

    public static Solver readSolverFromStream(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        try {
            return (Solver) stream.readUnshared();
        } catch (EOFException ex) {
            return null;
        }
    }

    public static List<Solver> readSolversFromFile(ZipFile file) throws IOException, ClassNotFoundException {
        ZipEntry entry = file.getEntry("solvers.edacc");
        if (entry == null) {
            throw new IOException("Invalid file.");
        }
        ObjectInputStream stream = new ObjectInputStream(file.getInputStream(entry));
        List<Solver> solvers = new LinkedList<Solver>();
        Solver solver;
        while ((solver = readSolverFromStream(stream)) != null) {
            solvers.add(solver);
        }
        return solvers;
    }

    public static HashMap<Integer, List<Solver>> mapFileSolversToExistingSolversWithSolverBinariesInCommon(List<Solver> fileSolvers) throws SQLException {
        HashMap<Integer, List<Solver>> map = new HashMap<Integer, List<Solver>>();
        List<Solver> dbSolvers = SolverDAO.getAll();
        for (Solver s : fileSolvers) {
            List<Solver> solverList = new ArrayList<Solver>();
            for (Solver dbSolver : dbSolvers) {
                boolean found = false;
                for (SolverBinaries dbSb : dbSolver.getSolverBinaries()) {
                    for (SolverBinaries sb : s.getSolverBinaries()) {
                        if (dbSb.getMd5().equals(sb.getMd5())) {
                            found = true;
                            break;
                        }

                    }
                    if (found) {
                        break;
                    }
                }
                if (found) {
                    for (Parameter p : s.parameters) {
                        found = false;
                        for (Parameter p2 : ParameterDAO.getParameterFromSolverId(dbSolver.getId())) {
                            if (p.getName().equals(p2.getName())) {
                                found = true;
                                break;
                            }
                        }
                        if (!found) {
                            break;
                        }
                    }
                    if (found) {
                        solverList.add(dbSolver);
                    }
                }
            }
            map.put(s.getId(), solverList);
        }
        return map;

    }

    public static HashMap<Integer, List<Solver>> mapFileSolversToExistingSolversWithSameParameters(List<Solver> fileSolvers) throws SQLException {
        HashMap<Integer, List<Solver>> map = new HashMap<Integer, List<Solver>>();
        List<Solver> dbSolvers = SolverDAO.getAll();
        for (Solver s : fileSolvers) {
            List<Solver> solverList = new ArrayList<Solver>();
            for (Solver dbSolver : dbSolvers) {
                boolean found = false;
                for (Parameter p : s.parameters) {
                    found = false;
                    for (Parameter p2 : ParameterDAO.getParameterFromSolverId(dbSolver.getId())) {
                        if (p.getName().equals(p2.getName())) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        break;
                    }
                }
                if (found) {
                    solverList.add(dbSolver);
                }
            }
            map.put(s.getId(), solverList);
        }
        return map;
    }

    public static InputStream getZippedCodeFile(Solver s) throws SQLException {
        final String query = "SELECT code FROM " + table + " WHERE idSolver=?";
        PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement(query);
        ps.setInt(1, s.getId());
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getBinaryStream("code");
        }
        return null;
    }
}
