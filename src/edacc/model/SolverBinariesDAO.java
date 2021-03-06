/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edacc.model;

import edacc.manageDB.Util;
import edacc.manageDB.NoSolverBinarySpecifiedException;
import edacc.util.Pair;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 *
 * @author dgall
 */
public class SolverBinariesDAO {

    private static final String TABLE = "SolverBinaries";
    private static final String INSERT_QUERY = "INSERT INTO " + TABLE + " (idSolver, binaryName, binaryArchive, md5, version, runCommand, runPath) VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE " + TABLE + " SET binaryName = ?, version = ?, runCommand = ?, runPath = ? WHERE idSolverBinary = ?";
    private static final String UPDATE_FILES_QUERY = "UPDATE " + TABLE + " SET binaryArchive = ?, md5 = ? WHERE idSolverBinary = ?";
    private static final String DELETE_QUERY = "DELETE FROM " + TABLE + " WHERE idSolverBinary=?";
    private static ObjectCache<SolverBinaries> cache = new ObjectCache<SolverBinaries>();

    public static void removeBinariesOfSolver(Solver solver) throws SQLException {
        final String query = "DELETE FROM " + TABLE + " WHERE idSolver=?";
        PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement(query);
        ps.setInt(1, solver.getId());
        ps.executeUpdate();
        // update object information
        for (SolverBinaries b : solver.getSolverBinaries()) {
            b.setDeleted();
        }
        // clear solver binaries vector of solver
        solver.setSolverBinaries(new Vector<SolverBinaries>());
    }

    public static void clearCache() {
        cache.clear();
    }

    private SolverBinariesDAO() {
    }

    public static void save(SolverBinaries s) throws SQLException, NoSolverBinarySpecifiedException, FileNotFoundException, IOException, NoSuchAlgorithmException {
        save(s, null);
    }

    public static void save(SolverBinaries s, BinaryData data) throws SQLException, NoSolverBinarySpecifiedException, FileNotFoundException, IOException, NoSuchAlgorithmException {
        if (s.isSaved()) {
            return;
        }

        PreparedStatement ps = null;

        if (s.isNew()) {
            ps = DatabaseConnector.getInstance().getConn().prepareStatement(INSERT_QUERY, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setInt(1, s.getIdSolver());
            ps.setString(2, s.getBinaryName());
            if (data != null) {
                ps.setBinaryStream(3, new ByteArrayInputStream(data.getData()));
            } else if (s.getBinaryFiles() != null && s.getBinaryFiles().length > 0) {
                ByteArrayOutputStream zipped = Util.zipFileArrayToByteStream(s.getBinaryFiles(), new File(s.getRootDir()));
                ps.setBinaryStream(3, new ByteArrayInputStream(zipped.toByteArray()));
            } else {
                throw new NoSolverBinarySpecifiedException();
            }
            ps.setString(4, s.getMd5());
            ps.setString(5, s.getVersion());
            ps.setString(6, s.getRunCommand());
            ps.setString(7, s.getRunPath());
        } else if (s.isModified()) {
            ps = DatabaseConnector.getInstance().getConn().prepareStatement(UPDATE_QUERY);
            ps.setString(1, s.getBinaryName());
            ps.setString(2, s.getVersion());
            ps.setString(3, s.getRunCommand());
            ps.setString(4, s.getRunPath());
            ps.setInt(5, s.getIdSolverBinary());
        } else if (s.isDeleted()) {
            ps = DatabaseConnector.getInstance().getConn().prepareStatement(DELETE_QUERY);
            ps.setInt(1, s.getIdSolverBinary());
        }
        ps.executeUpdate();
        if (s.isNew()) {
            // set id
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                s.setIdSolverBinary(rs.getInt(1));
            }
        }

        // modify files if necessary
        if (s.isModified() && s.getBinaryFiles() != null && s.getBinaryFiles().length > 0) { // binary files have been changed validly
            ps = DatabaseConnector.getInstance().getConn().prepareStatement(UPDATE_FILES_QUERY, PreparedStatement.RETURN_GENERATED_KEYS);
            ByteArrayOutputStream zipped = Util.zipFileArrayToByteStream(s.getBinaryFiles(), new File(s.getRootDir()));
            s.setMd5(Util.calculateMD5(new ByteArrayInputStream(zipped.toByteArray())));
            ps.setBinaryStream(1, new ByteArrayInputStream(zipped.toByteArray()));
            ps.setString(2, s.getMd5());
            ps.setInt(3, s.getId());
            ps.executeUpdate();
        }

        if (s.isDeleted()) {
            cache.remove(s);
            // remove SolverBinary from Vector in corresponding solver object
            SolverDAO.getById(s.getIdSolver()).removeSolverBinary(s);
            s.setNew();
        } else {
            cache.cache(s);
            s.setSaved();
        }
    }

    private static SolverBinaries getSolverBinaryFromResultSet(ResultSet rs) throws SQLException {
        SolverBinaries b = new SolverBinaries(rs.getInt("idSolver"));
        b.setIdSolverBinary(rs.getInt("idSolverBinary"));
        b.setBinaryName(rs.getString("binaryName"));
        b.setMd5(rs.getString("md5"));
        b.setVersion(rs.getString("version"));
        b.setRunCommand(rs.getString("runCommand"));
        b.setRunPath(rs.getString("runPath"));
        b.setBinaryArchive(null);
        b.setRootDir(null);
        return b;
    }

    public static Vector<SolverBinaries> getBinariesOfSolver(Solver solver) throws SQLException {
        final String query = "SELECT idSolverBinary, idSolver, binaryName, md5, version, runCommand, runPath FROM " + TABLE + " WHERE idSolver=?";
        PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement(query);
        ps.setInt(1, solver.getId());
        ResultSet rs = ps.executeQuery();
        Vector<SolverBinaries> res = new Vector<SolverBinaries>();
        while (rs.next()) {
            SolverBinaries c = cache.getCached(rs.getInt("idSolverBinary"));
            if (c != null) {
                res.add(c);
            } else {
                SolverBinaries b = getSolverBinaryFromResultSet(rs);
                cache.cache(b);
                res.add(b);
                b.setSaved();
            }
        }
        rs.close();
        return res;
    }

    public static Vector<SolverBinaries> getAll() throws SQLException {
        final String query = "SELECT idSolverBinary, idSolver, binaryName, md5, version, runCommand, runPath FROM " + TABLE;
        PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement(query);
        ResultSet rs = ps.executeQuery();
        Vector<SolverBinaries> res = new Vector<SolverBinaries>();
        while (rs.next()) {
            SolverBinaries c = cache.getCached(rs.getInt("idSolverBinary"));
            if (c != null) {
                res.add(c);
            } else {
                SolverBinaries b = getSolverBinaryFromResultSet(rs);
                cache.cache(b);
                res.add(b);
                b.setSaved();
            }
        }
        rs.close();
        return res;
    }

    public static SolverBinaries getById(int id) throws SQLException {
        SolverBinaries c = cache.getCached(id);
        if (c != null) {
            return c;
        }
        final String query = "SELECT idSolverBinary, idSolver, binaryName, md5, version, runCommand, runPath FROM " + TABLE + " WHERE idSolverBinary=?";
        PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement(query);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        SolverBinaries res = null;
        if (rs.next()) {
            SolverBinaries b = getSolverBinaryFromResultSet(rs);
            cache.cache(b);
            res = b;
            b.setSaved();
        }
        ps.close();
        rs.close();
        return res;
    }

    public static InputStream getZippedBinaryFile(SolverBinaries b) throws SQLException {
        final String query = "SELECT binaryArchive FROM " + TABLE + " WHERE idSolverBinary=?";
        PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement(query);
        ps.setInt(1, b.getId());
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getBinaryStream("binaryArchive");
        }
        return null;
    }

    public static ArrayList<SolverBinaries> getSolverBinariesInExperiment(Experiment experiment) throws SQLException {
        final String query = "SELECT DISTINCT idSolverBinary, idSolver, binaryName, md5, version, runCommand, runPath FROM " + TABLE + " "
                + "JOIN SolverConfig ON (idSolverBinary = SolverBinaries_idSolverBinary) "
                + "WHERE Experiment_idExperiment = ?";
        PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement(query);
        ps.setInt(1, experiment.getId());
        ResultSet rs = ps.executeQuery();
        ArrayList<SolverBinaries> res = new ArrayList<SolverBinaries>();
        while (rs.next()) {
            SolverBinaries c = cache.getCached(rs.getInt("idSolverBinary"));
            if (c != null) {
                res.add(c);
            } else {
                SolverBinaries b = getSolverBinaryFromResultSet(rs);
                cache.cache(b);
                res.add(b);
                b.setSaved();
            }
        }
        rs.close();
        return res;
    }

   /* public static HashMap<Integer, Integer> importSolverBinaries(ZipFile file, int solverId, HashMap<Integer, Integer> solverIdMap) throws IOException, ClassNotFoundException, SQLException, NoSolverBinarySpecifiedException, FileNotFoundException, NoSuchAlgorithmException {
        SolverBinariesDAO.clearCache();
        SolverDAO.clearCache();
        ZipEntry sbEntry = file.getEntry("solver_" + solverId + ".solverbinaries");
        Pair<SolverBinaries, BinaryData> sb;
        HashMap<Integer, Integer> res = new HashMap<Integer, Integer>();
        ObjectInputStream sbis = new ObjectInputStream(file.getInputStream(sbEntry));
        while ((sb = readSolverBinaryFromStream(sbis)) != null) {
            boolean found = false;
            System.out.println("FOUND SOLVER BINARY");
            Solver dbSolver = SolverDAO.getById(solverIdMap.get(sb.getFirst().getIdSolver()));
            for (SolverBinaries dbSb : dbSolver.getSolverBinaries()) {
                if (dbSb.realEquals(sb.getFirst())) {
                    found = true;
                    res.put(sb.getFirst().getId(), dbSb.getId());
                    break;
                }
            }
            if (!found) {
                // TODO:
                SolverBinaries dbSolverBinary = new SolverBinaries(sb.getFirst());
                dbSolverBinary.setIdSolver(solverIdMap.get(dbSolverBinary.getIdSolver()));
                System.out.println("WRITING SOLVERBINARY TO DB");
                save(dbSolverBinary, sb.getSecond());
                res.put(sb.getFirst().getId(), dbSolverBinary.getId());
            } 
        }
        SolverBinariesDAO.clearCache();
        SolverDAO.clearCache();
        return res;
    }*/

    public static void writeSolverBinariesToStream(ObjectOutputStream stream, List<SolverBinaries> solverBinaries) throws IOException, SQLException {
        for (SolverBinaries sb : solverBinaries) {
            stream.writeObject(sb.getId());
            InputStream is = getZippedBinaryFile(sb);
            BinaryData data = new BinaryData(is);
            is.close();
            stream.writeUnshared(data);
        }
    }

    public static Pair<Integer, BinaryData> readSolverBinaryDataFromStream(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        Integer sbId;
        try {
            sbId = (Integer) stream.readUnshared();
        } catch (EOFException ex) {
            return null;
        }
        BinaryData data = (BinaryData) stream.readObject();
        return new Pair<Integer, BinaryData>(sbId, data);
    }
}
