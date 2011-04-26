/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edacc.model;


import edacc.manageDB.Util;
import edacc.manageDB.NoSolverBinarySpecifiedException;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 * @author dgall
 */
public class SolverBinariesDAO {

    private static final String TABLE = "SolverBinaries";
    private static final String INSERT_QUERY = "INSERT INTO " + TABLE + " (idSolver, binaryName, binaryArchive, md5, version, runCommand, runPath) VALUES (?, ?, ?, ?, ?, ?, ?)";

    public void save(SolverBinaries s) throws SQLException, NoSolverBinarySpecifiedException, FileNotFoundException {
        if (s.isSaved()) {
            return;
        }

        PreparedStatement ps;

        if (s.isNew()) {
            ps = DatabaseConnector.getInstance().getConn().prepareStatement(INSERT_QUERY, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setInt(1, s.getIdSolver());
            ps.setString(2, s.getBinaryName());
            if (s.getBinaryArchive() != null && s.getBinaryArchive().length() > 0)
                ps.setBinaryStream(3, new FileInputStream(s.getBinaryArchive()));
            else
                throw new NoSolverBinarySpecifiedException();
            ps.setString(4, s.getMd5());
            ps.setString(5, s.getVersion());
            ps.setString(6, s.getRunCommand());
            ps.setString(7, s.getRunPath());
        } else if (s.isModified()) {
            
        } else if (s.isDeleted()) {
            
        }
    }
}
