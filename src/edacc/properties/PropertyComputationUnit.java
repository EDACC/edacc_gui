/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.properties;

import edacc.model.ComputationMethodDAO;
import edacc.model.ComputationMethodDoesNotExistException;
import edacc.model.DatabaseConnector;
import edacc.model.ExperimentResultDAO;
import edacc.model.ExperimentResultHasProperty;
import edacc.model.ExperimentResultHasPropertyDAO;
import edacc.model.InstanceDAO;
import edacc.model.InstanceHasProperty;
import edacc.model.InstanceHasPropertyDAO;
import edacc.model.InstanceNotInDBException;
import edacc.model.NoConnectionToDBException;
import edacc.model.Property;
import edacc.model.PropertyDAO;
import edacc.model.PropertyNotInDBException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author rretz
 */
public class PropertyComputationUnit implements Runnable {
    ExperimentResultHasProperty erhp;
    InstanceHasProperty ihp;
    PropertyComputationController callback;
    Property property;

    PropertyComputationUnit(ExperimentResultHasProperty erhp, PropertyComputationController callback) {
        this.erhp = erhp;
        this.callback = callback;
        this.property = erhp.getProperty();
    }

    PropertyComputationUnit(InstanceHasProperty ihp, PropertyComputationController callback) {
        this.ihp = ihp;
        this.callback = callback;
        this.property = ihp.getProperty();
    }

    @Override
    public void run() {
        if(erhp != null){
            try {
                Property property = erhp.getProperty();
                switch (property.getPropertySource()) {
                    case LauncherOutput:
                        compute(ExperimentResultDAO.getLauncherOutput(erhp.getExpResult()));
                        break;
                    case SolverOutput:
                        compute(ExperimentResultDAO.getSolverOutput(erhp.getExpResult()));
                        break;
                    case VerifierOutput:
                        compute(ExperimentResultDAO.getVerifierOutput(erhp.getExpResult()));
                        break;
                    case WatcherOutput:
                        compute(ExperimentResultDAO.getWatcherOutput(erhp.getExpResult()));
                        break;
                }
            } catch (NoConnectionToDBException ex) {
                Logger.getLogger(PropertyComputationUnit.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                Logger.getLogger(PropertyComputationUnit.class.getName()).log(Level.SEVERE, null, ex);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(PropertyComputationUnit.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(PropertyComputationUnit.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception e) { e.printStackTrace(); }
        }else if(ihp != null){
            try {
                switch (property.getPropertySource()) {
                    case Instance:
                        try {
                            compute(InstanceDAO.getBinary(ihp.getInstance().getId()));
                        } catch (InstanceNotInDBException ex) {
                            Logger.getLogger(PropertyComputationUnit.class.getName()).log(Level.SEVERE, null, ex);
                        } 
                        break;
                    case InstanceName:
                        parseInstanceName();
                        break;
                }
            } catch (NoConnectionToDBException ex) {
                Logger.getLogger(PropertyComputationUnit.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                Logger.getLogger(PropertyComputationUnit.class.getName()).log(Level.SEVERE, null, ex);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(PropertyComputationUnit.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(PropertyComputationUnit.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception e) { e.printStackTrace(); }
        }
        callback.callback();  
    }

    private void compute(Blob b) throws FileNotFoundException, IOException, SQLException, NoConnectionToDBException, InstanceNotInDBException, ComputationMethodDoesNotExistException {
        if(property.getComputationMethod() != null){

            // parse instance file (external program call)
            if (ihp != null) {
                File bin = ComputationMethodDAO.getBinaryOfComputationMethod(property.getComputationMethod());
                bin.setExecutable(true);
                Process p = Runtime.getRuntime().exec(bin.getAbsolutePath());
                Blob instance = InstanceDAO.getBinary(ihp.getInstance().getId());
                BufferedReader instanceReader = new BufferedReader(new InputStreamReader(instance.getBinaryStream()));
                // The std input stream of the external program. We pipe the content of the instance file into that stream
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
                // The std output stream of the external program (-> output of the program). We read the calculated value from this stream.
                BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));

                // pipe the content of the instance file to the input of the external program
                try {
                    int i;
                    while ((i = instanceReader.read()) != -1)
                        out.write(i);
                } catch (IOException e) {
                    // if a program stops reading from the stream, stop writing to it but show no error. Otherwise show an error message
                    if (!e.getMessage().contains("Broken pipe")) {
                        throw e;
                    }
                }
                /**
                 * Read the program output and save it as value of the property
                 * for the given instance.
                 * IMPORTANT!
                 * The value will be saved as a String in the DB and no
                 * conversion will be done!
                 * When loading the value from the DB, the method
                 * PropertyValueType.getJavaRepresentation(String) will be called
                 * on that value for the PropertyValueType of the property (so
                 * the String will be converted to the correct Java type).
                 */
                // Read first line of program output
                String value = in.readLine();
                // set the value and save it
                ihp.setValue(value);
                System.out.println(value);
            } else if (erhp != null){
                File bin = ComputationMethodDAO.getBinaryOfComputationMethod(property.getComputationMethod());
                bin.setExecutable(true);
                Process p = Runtime.getRuntime().exec(bin.getAbsolutePath());
                BufferedReader outputFileReader = new BufferedReader(new InputStreamReader(b.getBinaryStream()));
                // The std input stream of the external program. We pipe the content of the Blob b
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
                // The std output stream of the external program (-> output of the program). We read the calculated value from this stream.
                BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));

                // pipe the content of the output file to the input of the external program
                try {
                    int i;
                    while ((i = outputFileReader.read()) != -1)
                        out.write(i);
                } catch (IOException e) {
                    if (!e.getMessage().contains("Broken pipe")) {
                        throw e;
                    }
                }
                Vector<String> value = new Vector<String>();
                while(in.ready()){
                    value.add(in.readLine());
                }
                erhp.setValue(value);
                ExperimentResultHasPropertyDAO.save(erhp);
            }
        }else if(property.getRegularExpression() != null){
            /*Vector<String> res = new Vector<String>();
            BufferedReader buf = new BufferedReader(new InputStreamReader(b.getBinaryStream()));
            String tmp;
            Vector<String> toAdd = new Vector<String>();
            while((tmp = buf.readLine()) != null){
                if(!(toAdd = parse(tmp)).isEmpty()){
                    res.addAll(res);
                    if(!property.isMultiple() || ihp != null)
                        break;
                }
            }
            if(ihp  != null){
                ihp.setValue(res.firstElement());
                InstanceHasPropertyDAO.save(ihp);
            }
            else if(erhp != null){
                erhp.setValue(res);
                ExperimentResultHasPropertyDAO.save(erhp);
            }*/
        }
    }

    private Vector<String> parse(String toParse) {
        Vector<String> res = new Vector<String>();
        for(int i = 0; i < property.getRegularExpression().size(); i++){
            Pattern pat = Pattern.compile(property.getRegularExpression().get(i));
            Matcher m = pat.matcher(toParse);
            while (m.find()) {
                res.add(m.group(1));
                if(!property.isMultiple() || ihp != null)
                    return res;
            }    
        }
        return res;
    }

    private void parseInstanceName() {
        if(ihp != null){
            Vector<String> res = parse(ihp.getInstance().getName());
            if(!res.isEmpty())
                ihp.setValue(res.firstElement());
        }
    }

    public static void main(String[] args) {
        try {
            DatabaseConnector.getInstance().connect("edacc.informatik.uni-ulm.de", 3306, "edacc", "EDACC2", "edaccteam", false);

            PropertyComputationUnit unit = new PropertyComputationUnit(InstanceHasPropertyDAO.createInstanceHasInstanceProperty(InstanceDAO.getById(1), PropertyDAO.getById(1)), null);
            unit.compute(null);
        } catch (NoConnectionToDBException ex) {
            Logger.getLogger(PropertyComputationUnit.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(PropertyComputationUnit.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(PropertyComputationUnit.class.getName()).log(Level.SEVERE, null, ex);
        } catch (PropertyNotInDBException ex) {
            Logger.getLogger(PropertyComputationUnit.class.getName()).log(Level.SEVERE, null, ex);
        } catch (PropertyTypeNotExistException ex) {
            Logger.getLogger(PropertyComputationUnit.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ComputationMethodDoesNotExistException ex) {
            Logger.getLogger(PropertyComputationUnit.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
