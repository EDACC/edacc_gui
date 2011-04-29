/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edacc.model;


import edacc.manageDB.Util;
import edacc.manageDB.NoSolverBinarySpecifiedException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**solver.getBinaryFile() == null
 *
 * @author dgall
 */
public class SolverBinariesDAO {

    private static final String TABLE = "SolverBinaries";
    private static final String INSERT_QUERY = "INSERT INTO " + TABLE + " (idSolver, binaryName, binaryArchive, md5, version, runCommand, runPath) VALUES (?, ?, ?, ?, ?, ?, ?)";
   
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
            } else
                throw new NoSolverBinarySpecifiedException();
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
        s.setSaved();
    }
}
