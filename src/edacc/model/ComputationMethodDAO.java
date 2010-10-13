/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.model;

/**
 *
 * @author rretz
 */
public class ComputationMethodDAO {
    private static ObjectCache<ComputationMethod> cache = new ObjectCache<ComputationMethod>();
    private static String table = "ComputationMethod";
    private static String deleteQuery = "DELETE FROM " + table + "WHERE id=?;";
    private static String updateQuery = "UPDATE " + table + " SET name=?, description=?;";
    private static String insertQuery = "INSERT INTO " + table + "(name, md5, description, binaryName, binary) " +
            "VALUES (?, ?, ?, ?, ?);";

    public static ComputationMethod getById(int id){
        throw new UnsupportedOperationException("Not yet implemented");
    }

}
