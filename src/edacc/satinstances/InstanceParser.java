/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.satinstances;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author dgall
 */
public class InstanceParser {

    private static InstanceParser instance;

    private InstanceParser() { }

    public static InstanceParser getInstance() {
        if (instance == null)
            instance = new InstanceParser();
        return instance;
    }

    public SATInstance parseInstance(InputStream in) throws IOException, InvalidVariableException {
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            SATInstance satInstance = null;
            Vector<Integer> clause = new Vector<Integer>();
            while ((line = br.readLine()) != null) {
                if (line.startsWith("c")) {
                    continue;
                }
                if (line.startsWith("p")) {
                    String[] def = line.split(" ");
                    if (def.length >= 4) {
                        if (def[1].equals("cnf")) {
                            int numVariables = Integer.parseInt(def[2]);
                            int numClauses = Integer.parseInt(def[3]);
                            satInstance = new SATInstance(numClauses, numVariables);
                            continue;
                        }
                    }
                }
                if (satInstance != null) {
                    String[] cl = line.split(" ");
                    for (String s : cl) {
                        int i = Integer.parseInt(s);
                        if (i == 0) {
                            satInstance.addClause(clause);
                            clause = new Vector<Integer>();
                        } else {
                            clause.add(i);
                        }
                    }
                    satInstance.addClause(clause);
                }
            }
            try {
            System.out.println(satInstance.getClause(0));

        } catch (ClauseDoesntExistException ex) {
            Logger.getLogger(InstanceParser.class.getName()).log(Level.SEVERE, null, ex);
        }
             return satInstance;
    }

    public static void main(String[] args) throws IOException, InvalidVariableException {
        InstanceParser.getInstance().parseInstance(System.in);
    }
}
