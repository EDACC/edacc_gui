/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edacc.manageDB;

import edacc.model.SolverBinaries;
import java.io.File;
import java.util.ArrayList;
import javax.swing.AbstractListModel;

/**
 *
 * @author dgall
 */
public class SolverBinariesListModel extends AbstractListModel {

    private ArrayList<String> paths;

    private ArrayList<String> findAllPaths(File base, File f) {
        ArrayList<String> res = new ArrayList<String>();
        if (f.isDirectory()) {
            for (File file : f.listFiles()) {
                res.addAll(findAllPaths(base, file));
            }
        } else {
            String path = f.getPath().replace(base.getPath(), "");           
            path = path.replace(System.getProperty("file.separator"), "/");
            res.add(path);
        }
        return res;
    }

    public SolverBinariesListModel(SolverBinaries solverBin) {
        super();
        paths = new ArrayList<String>();
        for (File f : solverBin.getBinaryFiles()) {
            paths.addAll(findAllPaths(new File(solverBin.getRootDir()), new File(solverBin.getRootDir() + System.getProperty("file.separator") + f.getPath())));
        }
    }

    @Override
    public int getSize() {
        return paths.size();
    }

    @Override
    public Object getElementAt(int index) {
        if (index == -1) {
            return "/";
        }
        return paths.get(index);
    }
}
