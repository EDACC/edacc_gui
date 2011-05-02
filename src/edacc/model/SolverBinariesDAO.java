/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edacc.model;

import edacc.manageDB.Util;
import edacc.manageDB.NoSolverBinarySpecifiedException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

/**
 *
 * @author dgall
 */
public class SolverBinariesDAO {

    private static final String TABLE = "SolverBinaries";
    private static final String INSERT_QUERY = "INSERT INTO " + TABLE + " (idSolver, binaryName, binaryArchive, md5, version, runCommand, runPath) VALUES (?, ?, ?, ?, ?, ?, ?)";

    private static ObjectCache<SolverBinaries> cache = new ObjectCache<SolverBinaries>();

    private SolverBinariesDAO() {
        
    }

    public static void save(SolverBinaries s) throws SQLException, NoSolverBinarySpecifiedException, FileNotFoundException, IOException {
        if (s.isSaved()) {
            return;
        }

        PreparedStatement ps = null;

        if (s.isNew()) {
            ps = DatabaseConnector.getInstance().getConn().prepareStatement(INSERT_QUERY, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setInt(1, s.getIdSolver());
            ps.setString(2, s.getBinaryName());
            if (s.getBinaryFiles() != null && s.getBinaryFiles().length > 0) {
                ByteArrayOutputStream zipped = Util.zipFileArrayToByteStream(s.getBinaryFiles());
                ps.setBinaryStream(3, new ByteArrayInputStream(zipped.toByteArray()));
            } else {
                throw new NoSolverBinarySpecifiedException();
            }
            ps.setString(4, s.getMd5());
            ps.setString(5, s.getVersion());
            ps.setString(6, s.getRunCommand());
            ps.setString(7, s.getRunPath());
        } else if (s.isModified()) {
        } else if (s.isDeleted()) {
        }
        ps.executeUpdate();
        if (s.isNew()) {
            // set id
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                s.setIdSolverBinary(rs.getInt(1));
            }
        }
        cache.cache(s);
        s.setSaved();
    }

    private static SolverBinaries getSolverBinaryFromResultSet(ResultSet rs) throws SQLException {
        SolverBinaries b = new SolverBinaries(rs.getInt("idSolver"));
        b.setIdSolverBinary(rs.getInt("idSolverBinary"));
        b.setBinaryName(rs.getString("binaryName"));
        b.setMd5(rs.getString("md5"));
        b.setVersion(rs.getString("version"));
        b.setRunCommand(rs.getString("runCommand"));
        b.setRunPath(rs.getString("runPath"));

        return b;
    }

    public static Vector<SolverBinaries> getBinariesOfSolver(Solver solver) throws SQLException {
        final String query = "SELECT * FROM " + TABLE + " WHERE idSolver=?";
        PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement(query);
        ps.setInt(1, solver.getId());
        ResultSet rs = ps.executeQuery();
        Vector<SolverBinaries> res = new Vector<SolverBinaries>();
        while (rs.next()) {
            SolverBinaries c = cache.getCached(rs.getInt("idSolverBinary"));
            if (c != null)
                res.add(c);
            else {
                SolverBinaries b = getSolverBinaryFromResultSet(rs);
                cache.cache(b);
                res.add(b);
                b.setSaved();
            }
        }
        rs.close();
        return res;
    }
}
