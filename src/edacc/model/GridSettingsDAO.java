package edacc.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GridSettingsDAO {
    private static final String table = "gridSettings";

    public static void saveNumNodes(int numNodes) throws SQLException {
        PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement("UPDATE " + table + " SET numNodes=?");
        st.setInt(1, numNodes);
        st.executeUpdate();
    }

    public static void saveMaxRuntime(int maxRuntime) throws SQLException {
        PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement("UPDATE " + table + " SET maxRuntime=?");
        st.setInt(1, maxRuntime);
        st.executeUpdate();
    }

    public static void saveMaxJobsInQueue(int maxJobsInQueue) throws SQLException {
        PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement("UPDATE " + table + " SET maxJobsInQueue=?");
        st.setInt(1, maxJobsInQueue);
        st.executeUpdate();
    }

    public static int getNumNodes() throws SQLException {
        PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement("SELECT numNodes FROM " + table);
        ResultSet rs = st.executeQuery();
        if (rs.next()) {
            return rs.getInt("numNodes");
        }
        else
            throw new SQLException("grid settings table not initialized");
    }

    public static int getMaxRuntime() throws SQLException {
        PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement("SELECT maxRuntime FROM " + table);
        ResultSet rs = st.executeQuery();
        if (rs.next()) {
            return rs.getInt("maxRuntime");
        }
        else
            throw new SQLException("grid settings table not initialized");
    }

    public static int getMaxJobsInQueue() throws SQLException {
        PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement("SELECT maxJobsInQueue FROM " + table);
        ResultSet rs = st.executeQuery();
        if (rs.next()) {
            return rs.getInt("maxJobsInQueue");
        }
        else
            throw new SQLException("grid settings table not initialized");
    }


}
