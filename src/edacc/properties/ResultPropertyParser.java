/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.properties;

import edacc.model.ExperimentResultDAO;
import edacc.model.ExperimentResultHasSolverProperty;
import edacc.model.ExperimentResultHasSolverPropertyDAO;
import edacc.model.NoConnectionToDBException;
import edacc.model.SolverProperty;
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
class ResultPropertyParser implements Runnable {
    ExperimentResultHasSolverProperty toParse;
    ParserController callback;

    ResultPropertyParser(ExperimentResultHasSolverProperty toParse, ParserController callback) {
        this.toParse = toParse;
        this.callback = callback;
    }

    @Override
    public void run() {
        SolverProperty property = toParse.getSolvProperty();
        if(property.getSolverPropertyType().equals(SolverPropertyType.Parameter)){
            processParameter();
        }else {
            try {
                switch(property.getSolverPropertyType()){
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
                Logger.getLogger(ResultPropertyParser.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                Logger.getLogger(ResultPropertyParser.class.getName()).log(Level.SEVERE, null, ex);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(ResultPropertyParser.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(ResultPropertyParser.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
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
                    else if(token.equals(toParse.getSolvProperty().getPrefix()))
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
                    else if(token.equals(toParse.getSolvProperty().getPrefix()))
                        found = true;

                }
            }
        }
        toParse.setValue(res);
        ExperimentResultHasSolverPropertyDAO.save(toParse);
    }

    private void processParameter() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

}
