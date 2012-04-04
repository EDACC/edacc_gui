package edacc.model;

import edacc.manageDB.Util;
import edacc.util.Pair;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author simon
 */
public class CostDAO {

    private static final String costTable = "Cost";
    private static final String costInsertQuery = "INSERT INTO Cost (name) VALUES (?)";
    private static final String costUpdateQuery = "UPDATE Cost SET name = ? WHERE idCost = ?";
    private static final String costDeleteQuery = "DELETE FROM Cost WHERE idCost = ?";
    private static final String binaryTable = "CostBinary";
    private static final String binarySelect = "idCostBinary, Solver_idSolver, Cost_idCost, binaryName, md5, version, runCommand, runPath, parameters";
    private static final String binaryInsertQuery = "INSERT INTO CostBinary (Solver_idSolver, Cost_idCost, binaryName, binaryArchive, md5, version, runCommand, runPath, parameters) VALUES (?,?,?,?,?,?,?,?,?)";
    private static final String binaryUpdateQuery = "UPDATE CostBinary SET Cost_idCost = ?, binaryName = ?, md5 = ?, version = ?, runCommand = ?, runPath = ?, parameters = ? WHERE idCostBinary = ?";
    private static final String binaryDeleteQuery = "DELETE FROM CostBinary WHERE idCostBinary = ?";
    private static final String binaryUpdateFilesQuery = "UPDATE CostBinary SET binaryArchive = ?, md5 = ? WHERE idCostBinary = ?";
    private static final ObjectCache<Cost> costCache = new ObjectCache<Cost>();
    private static final ObjectCache<CostBinary> costBinaryCache = new ObjectCache<CostBinary>();

    private static Cost getCostByResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("idCost");
        String name = rs.getString("name");
        Cost cost = new Cost(id, name);
        return cost;
    }

    private static CostBinary getCostBinaryByResultSet(ResultSet rs) throws SQLException {
        CostBinary b = new CostBinary(rs.getInt("Solver_idSolver"));
        b.setIdCostBinary(rs.getInt("idCostBinary"));
        b.setCost(getCostById(rs.getInt("Cost_idCost")));
        b.setBinaryName(rs.getString("binaryName"));
        b.setMd5(rs.getString("md5"));
        b.setVersion(rs.getString("version"));
        b.setRunCommand(rs.getString("runCommand"));
        b.setRunPath(rs.getString("runPath"));
        b.setParameters(rs.getString("parameters"));
        return b;
    }

    public static List<Cost> getAllCosts() throws SQLException {
        Statement st = DatabaseConnector.getInstance().getConn().createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM " + costTable);
        List<Cost> res = new LinkedList<Cost>();
        while (rs.next()) {
            int id = rs.getInt("idCost");
            Cost c = costCache.getCached(id);
            if (c == null) {
                c = getCostByResultSet(rs);
                c.setSaved();
                costCache.cache(c);
            }
            res.add(c);
        }
        return res;
    }

    public static Cost getCostById(int id) throws SQLException {
        Cost c = costCache.getCached(id);
        if (c == null) {
            getAllCosts();
            c = costCache.getCached(id);
        }
        return c;
    }
    
    public static Cost getCostByName(String name) throws SQLException {
        for (Cost c : costCache.values()) {
            if (c.getName().equals(name)) {
                return c;
            }
        }
        getAllCosts();
        for (Cost c : costCache.values()) {
            if (c.getName().equals(name)) {
                return c;
            }
        }
        return null;
    }

    public static List<CostBinary> getCostBinariesForSolver(Solver s) throws SQLException {
        List<CostBinary> res = new LinkedList<CostBinary>();
        PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement("SELECT " + binarySelect + " FROM " + binaryTable + " WHERE Solver_idSolver = ?");
        ps.setInt(1, s.getId());
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            int id = rs.getInt("idCostBinary");
            CostBinary cb = costBinaryCache.getCached(id);
            if (cb == null) {
                cb = getCostBinaryByResultSet(rs);
                cb.setSaved();
            }
            res.add(cb);
        }
        return res;
    }

    public static void clearCache() {
        costCache.clear();
    }

    public static void saveCost(Cost c) throws SQLException {
        if (c.isSaved()) {
            return;
        }
        if (c.isNew()) {
            PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement(costInsertQuery, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setString(1, c.getName());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                c.setId(rs.getInt(1));
            }
            ps.close();
        } else if (c.isModified()) {
            PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement(costUpdateQuery);
            ps.setString(1, c.getName());
            ps.setInt(2, c.getId());
            ps.executeUpdate();
            ps.close();
        } else if (c.isDeleted()) {
            PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement(costDeleteQuery);
            ps.setInt(1, c.getId());
            ps.executeUpdate();
            ps.close();
        }
        c.setSaved();
    }

    public static void saveBinary(CostBinary b) throws SQLException, IOException, NoSuchAlgorithmException {
        if (b.isSaved()) {
            return;
        }
        if (b.getCost().isNew()) {
            saveCost(b.getCost());
        }

        PreparedStatement ps = null;

        if (b.isNew()) {
            ps = DatabaseConnector.getInstance().getConn().prepareStatement(binaryInsertQuery, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setInt(1, b.getIdSolver());
            ps.setInt(2, b.getCost().getId());
            ps.setString(3, b.getBinaryName());
            if (b.getBinaryFiles() != null && b.getBinaryFiles().length > 0) {
                ByteArrayOutputStream zipped = Util.zipFileArrayToByteStream(b.getBinaryFiles(), new File(b.getRootDir()));
                ps.setBinaryStream(4, new ByteArrayInputStream(zipped.toByteArray()));
            } else if (b.data != null) {
                ps.setBytes(4, b.data.getData());
            } else {
                throw new IllegalArgumentException("No cost binary specified.");
            }
            ps.setString(5, b.getMd5());
            ps.setString(6, b.getVersion());
            ps.setString(7, b.getRunCommand());
            ps.setString(8, b.getRunPath());
            ps.setString(9, b.getParameters());
        } else if (b.isModified()) {
            ps = DatabaseConnector.getInstance().getConn().prepareStatement(binaryUpdateQuery);
            ps.setInt(1, b.getCost().getId());
            ps.setString(2, b.getBinaryName());
            ps.setString(3, b.getVersion());
            ps.setString(4, b.getRunCommand());
            ps.setString(5, b.getRunPath());
            ps.setInt(6, b.getId());
        } else if (b.isDeleted()) {
            ps = DatabaseConnector.getInstance().getConn().prepareStatement(binaryDeleteQuery);
            ps.setInt(1, b.getId());
        }
        ps.executeUpdate();
        if (b.isNew()) {
            // set id
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                b.setIdCostBinary(rs.getInt(1));
            }
        }

        // modify files if necessary
        if (b.isModified() && b.getBinaryFiles() != null && b.getBinaryFiles().length > 0) { // binary files have been changed validly
            ps = DatabaseConnector.getInstance().getConn().prepareStatement(binaryUpdateFilesQuery, PreparedStatement.RETURN_GENERATED_KEYS);
            ByteArrayOutputStream zipped = Util.zipFileArrayToByteStream(b.getBinaryFiles(), new File(b.getRootDir()));
            b.setMd5(Util.calculateMD5(new ByteArrayInputStream(zipped.toByteArray())));
            ps.setBinaryStream(1, new ByteArrayInputStream(zipped.toByteArray()));
            ps.setString(2, b.getMd5());
            ps.setInt(3, b.getId());
            ps.executeUpdate();
        }

        if (b.isDeleted()) {
            costBinaryCache.remove(b);
            // remove SolverBinary from Vector in corresponding solver object
            SolverDAO.getById(b.getIdSolver()).removeCostBinary(b);
            b.setNew();
        } else {
            costBinaryCache.cache(b);
            b.setSaved();
        }
    }

    public static void saveCachedCosts() throws SQLException {
        boolean autocommit = DatabaseConnector.getInstance().getConn().getAutoCommit();
        try {
            DatabaseConnector.getInstance().getConn().setAutoCommit(false);
            for (Cost c : costCache.values()) {
                saveCost(c);
            }
        } catch (Throwable t) {
            if (autocommit) {
                DatabaseConnector.getInstance().getConn().rollback();
            }
            if (t instanceof SQLException) {
                throw (SQLException) t;
            }
            if (t instanceof Error) {
                throw (Error) t;
            }
        } finally {
            if (autocommit) {
                DatabaseConnector.getInstance().getConn().commit();
                DatabaseConnector.getInstance().getConn().setAutoCommit(autocommit);
            }
        }
    }

    public static InputStream getZippedBinaryFile(CostBinary b) throws SQLException {
        final String query = "SELECT binaryArchive FROM " + binaryTable + " WHERE idCostBinary=?";
        PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement(query);
        ps.setInt(1, b.getId());
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            try {
                return rs.getBinaryStream("binaryArchive");
            } finally {
                rs.close();
                ps.close();
            }
        }
        rs.close();
        ps.close();
        return null;
    }

    protected static void writeCostBinariesToStream(ObjectOutputStream stream, List<CostBinary> costBinaries) throws IOException, SQLException {
        for (CostBinary cb : costBinaries) {
            stream.writeObject(cb.getId());
            InputStream is = getZippedBinaryFile(cb);
            BinaryData data = new BinaryData(is);
            is.close();
            stream.writeUnshared(data);
        }
    }

    static Pair<Integer, BinaryData> readCostBinaryDataFromStream(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        Integer cbId;
        try {
            cbId = (Integer) stream.readUnshared();
        } catch (EOFException ex) {
            return null;
        }
        BinaryData data = (BinaryData) stream.readObject();
        return new Pair<Integer, BinaryData>(cbId, data);
    }
}
