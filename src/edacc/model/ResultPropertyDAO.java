/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.model;

import edacc.satinstances.PropertyValueTypeManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * data access object of the ResultProperty class
 * @author rretz
 */
public class ResultPropertyDAO {
     protected static final String table = "ResultProperty";
    private static final ObjectCache<ResultProperty> cache = new ObjectCache<ResultProperty>();

    /**
     * Returns and caches (if necessary) the requested ResultProperty object
     * @param id of the requested ResultProperty object
     * @return the requested ResultProperty object 
     * @throws NoConnectionToDBException
     * @throws SQLException
     * @throws ResultPropertyNotInDBException
     */
    public static ResultProperty getById(int id) throws NoConnectionToDBException, SQLException, ResultPropertyNotInDBException {
        ResultProperty res = cache.getCached(id);
        if(res != null){
            return res;
        }else{
            PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement(
                    "SELECT PropertyValueType_name, name, description, prefix "
                    + "FROM " + table + " WHERE idResultProperty=?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if(!rs.next())
                throw new ResultPropertyNotInDBException();
            res.setId(id);
            res.setValueType(PropertyValueTypeManager.getInstance().getPropertyValueTypeByName(rs.getString(1)));
            res.setName(rs.getString(2));
            res.setDescription(rs.getString(3));
            res.setPrefix(rs.getString(4));
            res.setSaved();
            cache.cache(res);
            return res;
        }
    }

}
