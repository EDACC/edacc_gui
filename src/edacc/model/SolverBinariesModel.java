/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.model;

import java.util.HashMap;
import java.util.LinkedList;

/**
 *
 * @author dgall
 */
public class SolverBinariesModel {

    private static SolverBinariesModel instance;
    private HashMap<Solver, LinkedList<SolverBinaries>> binaries;

    public SolverBinariesModel() {
        this.binaries = new HashMap<Solver, LinkedList<SolverBinaries>>();
    }

    public static SolverBinariesModel getInstance() {
        if (instance == null)
            instance = new SolverBinariesModel();
        return instance;
    }

    public void addSolverBinariesForSolver(Solver s, SolverBinaries b) {
        LinkedList<SolverBinaries> l = binaries.get(s);
        if (l == null) {
            l = new LinkedList<SolverBinaries>();
            binaries.put(s, l);
        }
        l.add(b);
    }

    public LinkedList<SolverBinaries> getBinariesForSolver(Solver s) {
        LinkedList<SolverBinaries> l = binaries.get(s);
        if (l != null)
            l = (LinkedList<SolverBinaries>) l.clone();
        return l;
    }

    public boolean solverHasBinary(Solver s) {
        LinkedList<SolverBinaries> b = binaries.get(s);
        return b != null && b.size() > 0;
    }
}
