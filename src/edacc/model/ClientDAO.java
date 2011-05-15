package edacc.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/**
 *
 * @author simon
 */
public class ClientDAO {

    protected static final String table = "Client";
    protected static final String selectQuery = "SELECT * FROM Clients";
    protected static final String updateQuery = "UPDATE " + table + " SET message =? WHERE idClient=?";
    protected static final String deleteQuery = "DELETE FROM " + table + " WHERE idExperiment=?";
    private static final ObjectCache<Client> cache = new ObjectCache<Client>();

    /**
     * persists a client object in the database
     * @param experiment The Experiment object to persist
     */
    public static void save(Client client) throws SQLException {
        if (client.isNew()) {
            throw new SQLException("Can''t insert clients.");
        }
        if (client.isModified()) {
        }
    }

    private static HashSet<Integer> getClientIds() throws SQLException {
        HashSet<Integer> res = new HashSet<Integer>();
        PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement("SELECT idClient FROM " + table);
        ResultSet rs = st.executeQuery();
        while (rs.next()) {
            res.add(rs.getInt("idClient"));
        }
        rs.close();
        st.close();
        return res;
    }

    private static String getIntArray(Collection<Integer> c) {
        String res = "(";
        Iterator<Integer> it = c.iterator();
        while (it.hasNext()) {
            res += "" + it.next();
            if (it.hasNext()) {
                res += ",";
            }
        }
        res += ")";
        return res;
    }

    public static synchronized ArrayList<Client> getClients() throws SQLException {
        ArrayList<Client> clients = new ArrayList<Client>();

        HashSet<Integer> clientIds = getClientIds();
        ArrayList<Integer> idsModified = new ArrayList<Integer>();
        ArrayList<Client> deletedClients = new ArrayList<Client>();

        for (Client c : cache.values()) {
            if (clientIds.contains(c.getId())) {
                idsModified.add(c.getId());
            } else {
                deletedClients.add(c);
            }
            clientIds.remove(c.getId());
        }

        if (!idsModified.isEmpty()) {
            PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement("SELECT idClient, message, TIMESTAMPDIFF(SECOND, lastReport, NOW()) > 20 AS dead FROM " + table + " WHERE idClient IN " + getIntArray(idsModified));
            ResultSet rs = st.executeQuery();
            while (rs.next()) {

                Client c = cache.getCached(rs.getInt("idClient"));
                c.setMessage(rs.getString("message"));
                c.setDead(rs.getBoolean("dead"));
            }
            rs.close();
            st.close();
        }

        if (!clientIds.isEmpty()) {
            PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement("SELECT idClient, numCores, numThreads, hyperthreading, turboboost, CPUName, cacheSize, cpuflags, memory, memoryFree, cpuinfo, meminfo, message, gridQueue_idgridQueue, lastReport, TIMESTAMPDIFF(SECOND, lastReport, NOW()) > 20 AS dead FROM " + table + " WHERE idClient IN " + getIntArray(clientIds));
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                Client c = new Client(rs);
                cache.cache(c);
            }
            rs.close();
            st.close();
        }
        for (Client c : cache.values()) {
            clients.add(c);
        }
        for (Client c : deletedClients) {
            c.setDeleted();
            c.notifyObservers();
            cache.remove(c);
        }

        PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement("SELECT Experiment_idExperiment, Client_idClient, numCores FROM Experiment_has_Client");
        ResultSet rs = st.executeQuery();
        HashMap<Client, HashMap<Experiment, Integer>> map = new HashMap<Client, HashMap<Experiment, Integer>>();
        while (rs.next()) {
            int clientId = rs.getInt("Client_idClient");
            int numCores = rs.getInt("numCores");
            Client c = cache.getCached(clientId);
            if (c == null) {
                continue;
            }
            Experiment exp = ExperimentDAO.getById(rs.getInt("Experiment_idExperiment"));
            HashMap<Experiment, Integer> tmp = map.get(c);
            if (tmp == null) {
                tmp = new HashMap<Experiment, Integer>();
                map.put(c, tmp);
            }
            tmp.put(exp, numCores);
        }
        for (Client c : cache.values()) {
            HashMap<Experiment, Integer> tmp = map.get(c);
            if (tmp != null) {
                c.setComputingExperiments(tmp);
            }
            if (c.isModified()) {
                c.notifyObservers();
                c.setSaved();
            }
        }
        return clients;
    }
}
