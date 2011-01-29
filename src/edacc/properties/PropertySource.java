/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.properties;

/**
 * Represent the three different kinds of SolverProperties.
 * The database representation is:
 * 0 for ResultFile
 * 1 for ClientOutput
 * 2 for Paramter
 * @author rretz
 */
public enum PropertySource {
    LauncherOutput, Parameter, SolverOutput, VerifierOutput, WatcherOutput, Instance, InstanceName;
}
