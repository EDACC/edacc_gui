/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.properties;

import edacc.model.ExperimentResult;
import edacc.model.ExperimentResultDAO;
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

/**
 * A class to parse the ResultFileProperties from a ResultFile.
 *
 * @author rretz
 */
public class FilePropertyParser {

    public FilePropertyParser(){        
    }

    /**
     * 
     *
     * @param solvProp to parse for
     * @param expResult on which the parser works (result file oder client output file)
     * @return values of all occurences of given SolverProperty in the given ExperimentResult.
     * @throws FileNotFoundException
     * @throws IOException
     * @throws NoAllowedSolverPropertyTypeException
     */
    public Vector<String> parse(SolverProperty solvProp, ExperimentResult expResult) throws FileNotFoundException, IOException, NoAllowedSolverPropertyTypeException, NoConnectionToDBException, SQLException{
        File file;
        if(solvProp.getSolverPropertyType() == SolverPropertyType.ResultFile)
            file = ExperimentResultDAO.getResultFile(expResult);
        else if(solvProp.getSolverPropertyType() == SolverPropertyType.ClientOutput)
            file = ExperimentResultDAO.getClientOutput(expResult);
        else
            throw new NoAllowedSolverPropertyTypeException();
        
        BufferedReader br = new BufferedReader(new FileReader(file));
        Vector<String> res = new Vector<String>();
        boolean found = false;
        String buffer;
        // Parse the complete file, because the Property can have multiple occurences in the file
        if(solvProp.isMultiple()){
            StringTokenizer t;
            while((buffer = br.readLine()) != null){
                t = new StringTokenizer(buffer);
                while(t.hasMoreTokens()){
                    String token = t.nextToken();
                    if(found){
                        res.add(token);
                        found = false;
                    }
                    else if(token.equals(solvProp.getPrefix()))
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
                    else if(token.equals(solvProp.getPrefix()))
                        found = true;
                    
                }
            } 
        }

        return res;
    }

}
