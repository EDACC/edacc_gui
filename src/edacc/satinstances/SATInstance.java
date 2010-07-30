/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.satinstances;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Vector;

/**
 * This class represents a SAT instance.
 * A SAT instance consists of clauses which contain certain literals.
 * In this class, a clause is represented by a list of integers. Every integer i
 * in the list stands for a variable with the index i. A negative value of i
 * means that the variable i is negated in the clause.
 * The indices of the literals can be every int value except zero.
 * For the indices c of the clauses; 0 <= c < getNumClauses().
 *
 * @author dgall
 */
public class SATInstance extends PropertyInput {

    private Vector<Vector<Integer>> clauses;
    private Vector<Integer> variables;

    /**
     * Creates an empty SAT instance.
     * @param numClauses
     * @param numVariables
     */
    public SATInstance(int numClauses, int numVariables) {
        clauses = new Vector<Vector<Integer>>();
        variables = new Vector<Integer>();
    }

    /**
     * Returns the clause with the index i.
     * @param index
     * @return
     * @throws ClauseDoesntExistException if the clause with the specified index
     * doesn't exist.
     */
    public Vector<Integer> getClause(int index) throws ClauseDoesntExistException {
        if (index < 0 || index >= clauses.size())
            throw new ClauseDoesntExistException();
        return (Vector<Integer>) clauses.get(index).clone();
    }

    /**
     * Returns the number of clauses, in which the variable index occurs.
     * @param index
     * @return
     * @throws VariableDoesntExistException if the variable doesn't exist.
     */
    public int getNumOccurrenceOfVariable(int index) throws VariableDoesntExistException {
        return getNumOccurenceOfLiteral(index) + getNumOccurenceOfLiteral(-index);
    }

    /**
     * Returns the number of clauses, in which the given literal occurs.
     * @param literal
     * @return
     * @throws VariableDoesntExistException
     */
    public int getNumOccurenceOfLiteral(int literal) throws VariableDoesntExistException {
        if (literal < 0 ? variables.contains(-literal) : variables.contains(literal))
            throw new VariableDoesntExistException();
        int count = 0;
        for (int i = 0; i < clauses.size(); i++) {
            if (clauses.get(i).contains(literal))
                count++;
        }
        return count;
    }

    /**
     * Returns the indices of the clauses containing the variable with the
     * given index.
     * @param index
     * @return
     * @throws VariableDoesntExistException
     */
    public LinkedList<Integer> getClausesOfVariable(int index) throws VariableDoesntExistException {
        LinkedList<Integer> res = getClausesOfLiteral(index);
        for (int i : getClausesOfLiteral(-index))
            if (!res.contains(i)) // don't add duplicates
                res.add(i);
        return res;
    }

    /**
     * Returns the indices of the clauses containing the given literal.
     * @param index
     * @return
     * @throws VariableDoesntExistException
     */
    public LinkedList<Integer> getClausesOfLiteral(int index) throws VariableDoesntExistException {
        if (index <= -getNumVariables() || index >= getNumVariables())
                    throw new VariableDoesntExistException();
        LinkedList<Integer> res = new LinkedList<Integer>();
        for (int i = 0; i < clauses.size(); i++)
            if (clauses.get(i).contains(index))
                res.add(i);
        return res;
    }

    /**
     *
     * @return the number of variables in this SAT instance.
     */
    public int getNumVariables() {
        return variables.size();
    }

    /**
     *
     * @return the number of clauses in this SAT instance.
     */
    public int getNumClauses() {
        return clauses.size();
    }

    /**
     * Adds a new clause to the instance.
     * @param clause the clause to be added.
     * @return the index of the new clause.
     */
    public int addClause(Vector<Integer> clause) throws InvalidVariableException {
        if (clause.contains(0))
            throw new InvalidVariableException();
        clauses.add(clause);
        updateVariables();
        return clauses.size() - 1;
    }

    /**
     * Removes the clause with index index.
     * @param index
     * @return if the removal of the clause has been succesful.
     */
    public boolean removeClause(int index) {
        try {
            clauses.remove(index);
        } catch (ArrayIndexOutOfBoundsException e) {
            return false;
        }
        return true;
    }

    /**
     * Sets the value of a clause.
     * @param index
     * @param clause
     * @throws ClauseDoesntExistException if the index is < 0 or > numClauses()
     */
    public void setClause(int index, Vector<Integer> clause) throws ClauseDoesntExistException {
        if (index < 0 || index >= clauses.size())
            throw new ClauseDoesntExistException();
        clauses.set(index, clause);
    }

    private void updateVariables() {
        HashMap<Integer, Boolean> map = new HashMap<Integer, Boolean>();
        for (Vector<Integer> clause : clauses)
            for (int lit : clause) {
                if (lit < 0)
                    lit = -lit;
                // mark variable as counted
                map.put(lit, true);
            }
         variables = new Vector<Integer>(map.keySet());
    }
}
