package edacc.model;

import java.sql.*;

/**
 * singleton class handling the database connection
 * @author daniel
 */
public class DatabaseConnector {
    private static DatabaseConnector instance = null;
    protected Connection conn;

    private DatabaseConnector() {}

    public static DatabaseConnector getInstance() {
        if (instance == null) instance = new DatabaseConnector();
        return instance;
    }

    public void connect(String hostname, int port, String username, String database, String password) throws ClassNotFoundException, SQLException {
        if (conn != null) conn.close();
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://" + hostname + ":" + port + "/" + database + "?user=" + username + "&password=" + password);
        }
        catch (ClassNotFoundException e) {
            throw e;
        }
        catch (SQLException e) {
            throw e;
        }
        
    }



}
