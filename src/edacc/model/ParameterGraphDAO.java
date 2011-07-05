package edacc.model;

import edacc.parameterspace.graph.ParameterGraph;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

/**
 *
 * @author simon
 */
public class ParameterGraphDAO {

    public static final String INSERT = "INSERT INTO ParameterGraph (Solver_idSolver, serializedGraph) VALUES (?, ?)";
    public static final String UPDATE = "UPDATE ParameterGraph SET serializedGraph = ? WHERE Solver_idSolver = ?";

    /**
     * Loads the parameter graph object of the solver binary selected in the configuration experiment
     * specified by the idExperiment argument. Parameter graphs objects provide methods
     * to build, modify and validate solver parameters.
     * @param idExperiment ID of the configuration experiment
     * @return parameter graph object providing parameter space methods.
     * @throws SQLException 
     */
    public static ParameterGraph loadParameterGraph(Solver solver) throws SQLException, JAXBException {
        Statement st = DatabaseConnector.getInstance().getConn().createStatement();
        ResultSet rs = st.executeQuery("SELECT serializedGraph FROM ParameterGraph WHERE Solver_idSolver = " + solver.getId());
        try {
            if (rs.next()) {
                return unmarshal(ParameterGraph.class, rs.getBlob("serializedGraph").getBinaryStream());
            }
        } finally {
            rs.close();
            st.close();
        }
        return null;
    }

    public static void saveParameterGraph(ParameterGraph graph, Solver solver) throws SQLException, JAXBException {
        PreparedStatement st;
        if (loadParameterGraph(solver) != null) {
            st = DatabaseConnector.getInstance().getConn().prepareStatement(UPDATE);
            st.setInt(2, solver.getId());
            st.setBinaryStream(1, marshall(ParameterGraph.class, graph));
        } else {
            st = DatabaseConnector.getInstance().getConn().prepareStatement(INSERT);
            st.setInt(1, solver.getId());
            st.setBinaryStream(2, marshall(ParameterGraph.class, graph));
        }
        st.executeUpdate();
    }

    /**
     * Generic XML unmarshalling method.
     * @param docClass
     * @param inputStream
     * @return
     * @throws JAXBException
     */
    private static <T> T unmarshal(Class<T> docClass, InputStream inputStream) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(docClass);
        Unmarshaller u = jc.createUnmarshaller();
        return (T) u.unmarshal(inputStream);
    }

    /**
     * Generic XML marshalling method.
     * @param <T>
     * @param docClass
     * @param object
     * @return
     * @throws JAXBException 
     */
    private static <T> InputStream marshall(Class<T> docClass, T object) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(docClass);
        Marshaller m = jc.createMarshaller();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        m.marshal(object, os);
        return new ByteArrayInputStream(os.toByteArray());
    }
}
