/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.properties;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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
     * Parses the properties of the given prefixes from the file given by the filePath.
     * Only the first appearance of each prefix is used, any other will be ignored.
     * @param prefix Vector of perfixes to parse for.
     * @param filePath the path of the file on which the parser shall work.
     * @return Array with the results of all prefixs used at the given file in ordner of the given prefix Vector
     */
    public String[] parse(Vector<String> prefix, String filePath) throws FileNotFoundException, IOException{
        File file = new File(filePath);
        String[] result = new String[prefix.size()];
        BufferedReader br = new BufferedReader(new FileReader(file));
        
        // iterate over all lines of the given file and parse for the given prefix
        String line;
        int found = -1;
        while((line = br.readLine()) != null){
            StringTokenizer t = new StringTokenizer(line);
            while(t.hasMoreTokens()){
                String token = t.nextToken();
                
                // add the current token to the result array when the previous token was one of the prefixes
                if(found != -1){
                    result[found] = token;
                    found = -1;
                } else{
                   for(int i = 0; i < prefix.size(); i++){
                        if(result[i] == null){
                            if(token.equals(prefix.get(i))){
                               found = i;
                               break;
                            }
                        }
                    }

                }
                
            }

        }
        return result;
    }

}
