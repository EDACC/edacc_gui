/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.properties;

import edacc.model.ExperimentResultDAO;
import edacc.model.ExperimentResultHasProperty;
import edacc.model.ExperimentResultHasPropertyDAO;
import edacc.model.NoConnectionToDBException;
import edacc.model.Property;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author rretz
 */
class PropertyComputationUnit implements Runnable {
    ExperimentResultHasProperty toParse;
    PropertyComputationController callback;

    PropertyComputationUnit(ExperimentResultHasProperty toParse, PropertyComputationController callback) {
        this.toParse = toParse;
        this.callback = callback;
    }

    @Override
    public void run() {
       // try {
            /*Property property = toParse.getSolvProperty();
            if(property.getPropertySource().equals(SolverPropertyType.Parameter)){
            processParameter();
            }else {
            try {
            switch(property.getPropertySource()){
            case LauncherOutput:
            parse(ExperimentResultDAO.getLauncherOutputFile(toParse.getExpResult()));
            break;
            case SolverOutput:
            parse(ExperimentResultDAO.getSolverOutputFile(toParse.getExpResult()));
            break;
            case VerifierOutput:
            parse(ExperimentResultDAO.getVerifierOutputFile(toParse.getExpResult()));
            break;
            case WatcherOutput:
            parse(ExperimentResultDAO.getWatcherOutputFile(toParse.getExpResult()));
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
            }
            }
            callback.callback();*/
    /*    } catch (InterruptedException ex) {
            Logger.getLogger(PropertyComputationUnit.class.getName()).log(Level.SEVERE, null, ex);
        }
        /*Property property = toParse.getSolvProperty();
        if(property.getPropertySource().equals(SolverPropertyType.Parameter)){
            processParameter();
        }else {
            try {
                switch(property.getPropertySource()){
                    case LauncherOutput:
                        parse(ExperimentResultDAO.getLauncherOutputFile(toParse.getExpResult()));
                        break;
                    case SolverOutput:
                        parse(ExperimentResultDAO.getSolverOutputFile(toParse.getExpResult()));
                        break;
                    case VerifierOutput:
                        parse(ExperimentResultDAO.getVerifierOutputFile(toParse.getExpResult()));
                        break;
                    case WatcherOutput:
                        parse(ExperimentResultDAO.getWatcherOutputFile(toParse.getExpResult()));
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
            }
        }*/
        System.out.println("arbeit, arbeit!");
        callback.callback();
        
    }


    private void parse(File f) throws FileNotFoundException, IOException, NoConnectionToDBException, SQLException {
        BufferedReader br = new BufferedReader(new FileReader(f));
        Vector<String> res = new Vector<String>();
        boolean found = false;
        String buffer;
        // Parse the complete file, because the Property can have multiple occurences in the file
        if(toParse.getSolvProperty().isMultiple()){
            StringTokenizer t;
            while((buffer = br.readLine()) != null){
                t = new StringTokenizer(buffer);
                while(t.hasMoreTokens()){
                    String token = t.nextToken();
                    if(found){
                        res.add(token);
                        found = false;
                    }
                    else if(token.equals(toParse.getSolvProperty().getRegularExpression()))
                        found = true;

                }
            }
        // Only parse to the first occurnce of the prefix, because the Property only have one occurence per file
        }else {
            StringTokenizer t;
            while((buffer = br.readLine()) != null){
                t = new StringTokenizer(buffer);
                while(t.hasMoreTokens()){
                    String token = t.nextToken();
                    if(found){
                        res.add(token);
                        break;
                    }
                    else if(token.equals(toParse.getSolvProperty().getRegularExpression()))
                        found = true;

                }
            }
        }
        toParse.setValue(res);
        ExperimentResultHasPropertyDAO.save(toParse);
    }

    private void processParameter() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

}
