package edacc.model;

import edacc.manageDB.Util;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 *
 * @author simon
 */
public class VerifierDAO {

    private static final String table = "Verifier";
    private static final String selectQuery = "SELECT idVerifier, name, description, md5, runCommand, runPath FROM " + table;
    private static final ObjectCache<Verifier> cache = new ObjectCache<Verifier>();

    private static String getInsertQuery(int count) {
        StringBuilder query = new StringBuilder("INSERT INTO " + table + " "
                + "(name,binaryArchive,description,md5,runCommand,runPath) "
                + "VALUES (?,?,?,?,?,?)");
        count--;
        for (int i = 0; i < count; i++) {
            query.append(",(?,?,?,?,?,?)");
        }
        return query.toString();
    }

    private static String getUpdateQuery(int count) {
        // create a dummy header (there is no dataset with idVerifier == -1)
        StringBuilder constTable = new StringBuilder("(SELECT -1 AS idVerifier, \"\" AS name, \"\" AS description, \"\" AS md5, \"\" AS runCommand, \"\" AS runPath");
        // append rows to constTable
        for (int i = 0; i < count; i++) {
            constTable.append(" UNION SELECT ?,?,?,?,?,?");
        }
        constTable.append(")");

        StringBuilder query = new StringBuilder("UPDATE " + table + " AS t1, ");
        query.append(constTable);
        query.append(" AS t2 ");
        query.append("SET t1.name=t2.name, t1.description=t2.description,t1.md5=t2.md5,t1.runCommand=t2.runCommand,t1.runPath=t2.runPath ");
        query.append("WHERE t1.idVerifier=t2.idVerifier");
        return query.toString();
    }

    private static String getUpdateFilesQuery(int count) {
        // create a dummy header (there is no dataset with idVerifier == -1)
        StringBuilder constTable = new StringBuilder("(SELECT -1 AS idVerifier, \"\" AS binaryArchive");
        // append rows to constTable
        for (int i = 0; i < count; i++) {
            constTable.append(" UNION SELECT ?,?");
        }
        constTable.append(")");

        StringBuilder query = new StringBuilder("UPDATE " + table + " AS t1, ");
        query.append(constTable);
        query.append(" AS t2 ");
        query.append("SET t1.binaryArchive=t2.binaryArchive ");
        query.append("WHERE t1.idVerifier=t2.idVerifier");
        return query.toString();
    }

    private static String getDeleteQuery(int count) {
        StringBuilder query = new StringBuilder("DELETE FROM " + table + " WHERE idVerifier IN (?");
        count--;
        for (int i = 0; i < count; i++) {
            query.append(",?");
        }
        query.append(')');
        return query.toString();
    }

    public static void saveAll(List<Verifier> verifiers) throws SQLException {
        boolean autoCommit = DatabaseConnector.getInstance().getConn().getAutoCommit();
        DatabaseConnector.getInstance().getConn().setAutoCommit(false);
        try {
            List<Verifier> newVerifiers = new LinkedList<Verifier>();
            List<Verifier> modifiedVerifiers = new LinkedList<Verifier>();
            List<Verifier> verifiersWithModifiedFiles = new LinkedList<Verifier>();
            List<Verifier> deletedVerifiers = new LinkedList<Verifier>();
            for (Verifier v : verifiers) {
                if (v.isNew()) {
                    newVerifiers.add(v);
                } else if (v.isModified()) {
                    modifiedVerifiers.add(v);
                    if (v.getFiles() != null && v.getFiles().length != 0) {
                        verifiersWithModifiedFiles.add(v);
                    }
                } else if (v.isDeleted()) {
                    deletedVerifiers.add(v);
                }
            }

            if (!newVerifiers.isEmpty()) {
                String query = getInsertQuery(newVerifiers.size());
                PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
                int curCount = 1;
                for (Verifier v : newVerifiers) {
                    st.setString(curCount++, v.getName());
                    if (v.getFiles() != null) {
                        ByteArrayOutputStream zipped = Util.zipFileArrayToByteStreamAutoBasePath(v.getFiles());
                        st.setBinaryStream(curCount++, new ByteArrayInputStream(zipped.toByteArray()));
                    } else if (v.data != null) {
                        st.setBytes(curCount++, v.data.getData());
                    } else {
                        throw new IllegalArgumentException("No binary specified.");
                    }
                    st.setString(curCount++, v.getDescription());
                    st.setString(curCount++, v.getMd5());
                    st.setString(curCount++, v.getRunCommand());
                    st.setString(curCount++, v.getRunPath());
                }
                st.executeUpdate();
                ResultSet rs = st.getGeneratedKeys();
                int i = 0;
                while (rs.next()) {
                    newVerifiers.get(i).setId(rs.getInt(1));
                    newVerifiers.get(i).setSaved();
                    final Verifier v = newVerifiers.get(i);
                    cache.cache(v);
                    DatabaseConnector.getInstance().addRollbackOperation(new Runnable() {

                        @Override
                        public void run() {
                            v.setNew();
                            cache.remove(v);
                        }
                    });
                    i++;
                }
                rs.close();
                st.close();
            }
            if (!modifiedVerifiers.isEmpty()) {
                String query = getUpdateQuery(modifiedVerifiers.size());
                PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement(query);
                int curCount = 1;
                for (Verifier v : modifiedVerifiers) {
                    st.setInt(curCount++, v.getId());
                    st.setString(curCount++, v.getName());
                    st.setString(curCount++, v.getDescription());
                    st.setString(curCount++, v.getMd5());
                    st.setString(curCount++, v.getRunCommand());
                    st.setString(curCount++, v.getRunPath());

                    v.setSaved();
                    final Verifier ve = v;
                    DatabaseConnector.getInstance().addRollbackOperation(new Runnable() {

                        @Override
                        public void run() {
                            ve.setModified();
                        }
                    });
                }
                st.executeUpdate();
                st.close();
            }
            if (!verifiersWithModifiedFiles.isEmpty()) {
                String query = getUpdateFilesQuery(verifiersWithModifiedFiles.size());
                PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement(query);
                int curCount = 1;
                for (Verifier v : verifiersWithModifiedFiles) {
                    st.setInt(curCount++, v.getId());
                    ByteArrayOutputStream zipped = Util.zipFileArrayToByteStreamAutoBasePath(v.getFiles());
                    st.setBinaryStream(curCount++, new ByteArrayInputStream(zipped.toByteArray()));
                    v.setSaved();

                    final Verifier ve = v;
                    DatabaseConnector.getInstance().addRollbackOperation(new Runnable() {

                        @Override
                        public void run() {
                            ve.setModified();
                        }
                    });
                }
                st.executeUpdate();
                st.close();
            }
            if (!deletedVerifiers.isEmpty()) {
                String query = getDeleteQuery(deletedVerifiers.size());
                PreparedStatement st = DatabaseConnector.getInstance().getConn().prepareStatement(query);
                int curCount = 1;
                for (Verifier v : deletedVerifiers) {
                    st.setInt(curCount++, v.getId());
                }
                st.executeUpdate();
                st.close();
            }

            for (Verifier v : verifiers) {
                for (VerifierParameter vp : v.getParameters()) {
                    vp.setIdVerifier(v.getId());
                }
            }

            VerifierParameterDAO.saveAll(verifiers);
        } catch (Throwable t) {
            if (autoCommit) {
                DatabaseConnector.getInstance().rollback();
            }
            if (t instanceof Error) {
                throw (Error) t;
            } else if (t instanceof SQLException) {
                throw (SQLException) t;
            }
        } finally {
            if (autoCommit) {
                DatabaseConnector.getInstance().commit();
                DatabaseConnector.getInstance().getConn().setAutoCommit(autoCommit);
            }
        }
    }

    private static Verifier getVerifierFromResultSet(ResultSet rs, Map<Integer, List<VerifierParameter>> verifierParameterMap) throws SQLException {
        Verifier verifier = new Verifier();
        verifier.setId(rs.getInt("idVerifier"));
        verifier.setName(rs.getString("name"));
        verifier.setDescription(rs.getString("description"));
        verifier.setMd5(rs.getString("md5"));
        verifier.setRunCommand(rs.getString("runCommand"));
        verifier.setRunPath(rs.getString("runPath"));

        verifier.setParameters(verifierParameterMap.get(verifier.getId()));
        return verifier;
    }

    public static List<Verifier> getAllVerifiers() throws SQLException {
        List<Verifier> res = new ArrayList<Verifier>();
        Statement st = DatabaseConnector.getInstance().getConn().createStatement();
        Map<Integer, List<VerifierParameter>> verifierParameterMap = VerifierParameterDAO.getAll();
        ResultSet rs = st.executeQuery(selectQuery);
        while (rs.next()) {
            int id = rs.getInt("idVerifier");
            Verifier v = cache.getCached(id);
            if (v == null) {
                v = getVerifierFromResultSet(rs, verifierParameterMap);
                v.setSaved();
                cache.cache(v);
            }
            res.add(v);
        }
        rs.close();
        st.close();
        return res;
    }

    public static Verifier getById(int id) throws SQLException {
        Verifier v = cache.getCached(id);
        if (v == null) {
            getAllVerifiers();
            v = cache.getCached(id);
        }
        return v;
    }

    public static void clearCache() {
        cache.clear();
    }

    public static void saveAllCached() throws SQLException {
        List<Verifier> v = new ArrayList<Verifier>();
        v.addAll(cache.values());
        saveAll(v);
    }

    public static void exportVerifiers(Tasks task, ZipOutputStream stream, List<Verifier> verifiers) throws SQLException, IOException {
        task.setOperationName("Exporting verifiers..");
        int current = 1;
        for (Verifier v : verifiers) {
            task.setStatus("Writing verifier binary " + current + " / " + verifiers.size());
            task.setTaskProgress(current / (float) verifiers.size());

            stream.putNextEntry(new ZipEntry("verifier_" + v.getId() + ".binary"));
            VerifierDAO.writeVerifierBinaryToStream(new ObjectOutputStream(stream), v);
            current++;
        }
        task.setTaskProgress(0.f);
        task.setStatus("Writing verifier informations..");
        stream.putNextEntry(new ZipEntry("verifiers.edacc"));
        writeVerifiersToStream(new ObjectOutputStream(stream), verifiers);

        task.setStatus("Done.");
    }

    private static void writeVerifierBinaryToStream(ObjectOutputStream stream, Verifier v) throws IOException, SQLException {
        InputStream is = getZippedBinaryFile(v);
        BinaryData data = new BinaryData(is);
        is.close();
        stream.writeUnshared(data);
    }
    
    private static BinaryData readVerifierBinaryDataFromStream(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        return (BinaryData) stream.readObject();
    }

    private static InputStream getZippedBinaryFile(Verifier v) throws SQLException {
        final String query = "SELECT binaryArchive FROM " + table + " WHERE idVerifier=?";
        PreparedStatement ps = DatabaseConnector.getInstance().getConn().prepareStatement(query);
        ps.setInt(1, v.getId());
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

    private static void writeVerifiersToStream(ObjectOutputStream stream, List<Verifier> verifiers) throws IOException {
        for (Verifier v : verifiers) {
            stream.writeUnshared(v);
        }
    }

    public static List<Verifier> readVerifiersFromFile(ZipFile file) throws IOException, ClassNotFoundException {
        ZipEntry entry = file.getEntry("verifiers.edacc");
        if (entry == null) {
            throw new IOException("Invalid file.");
        }
        ObjectInputStream stream = new ObjectInputStream(file.getInputStream(entry));
        List<Verifier> verifiers = new LinkedList<Verifier>();
        Verifier verifier;
        while ((verifier = readVerifiersFromStream(stream)) != null) {
            verifiers.add(verifier);
        }
        return verifiers;
    }

    private static Verifier readVerifiersFromStream(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        try {
            return (Verifier) stream.readUnshared();
        } catch (EOFException ex) {
            return null;
        }
    }

    public static HashMap<Integer, List<Verifier>> mapFileVerifiersToExistingVerifiers(List<Verifier> fileVerifiers) throws SQLException {
        HashMap<Integer, List<Verifier>> res = new HashMap<Integer, List<Verifier>>();
        List<Verifier> dbVerifiers = getAllVerifiers();

        for (Verifier f : fileVerifiers) {
            for (Verifier v : dbVerifiers) {
                if (v.realEquals(f)) {
                    List<Verifier> verifiers = res.get(f.getId());
                    if (verifiers == null) {
                        verifiers = new LinkedList<Verifier>();
                        res.put(f.getId(), verifiers);
                    }
                    verifiers.add(v);
                }
            }
        }
        return res;
    }

    public static HashMap<Integer, Verifier> importVerifiers(Tasks task, ZipFile file, List<Verifier> verifiers, HashMap<Integer, Verifier> verifierMap, HashMap<Integer, String> nameMap) throws SQLException, IOException, ClassNotFoundException {
        HashMap<Integer, Verifier> res = new HashMap<Integer, Verifier>();

        task.setOperationName("Importing verifiers..");
        clearCache();
        int current = 1;
        for (Verifier v : verifiers) {
            task.setStatus("Saving verifier " + current + " / " + verifiers.size());
            task.setTaskProgress(current / (float) verifiers.size());
            if (verifierMap.containsKey(v.getId())) {
            } else {
                Verifier newDBVerifier = new Verifier(v);
                newDBVerifier.setName(nameMap.get(v.getId()));
                ZipEntry entry = file.getEntry("verifier_" + v.getId() + ".binary");
                ObjectInputStream ois = new ObjectInputStream(file.getInputStream(entry));
                newDBVerifier.data = VerifierDAO.readVerifierBinaryDataFromStream(ois);
                newDBVerifier.setNew();
                List<Verifier> tmp = new LinkedList<Verifier>();
                tmp.add(newDBVerifier);
                VerifierDAO.saveAll(tmp);
                res.put(v.getId(), newDBVerifier);
            }
        }
        return res;
    }
}
