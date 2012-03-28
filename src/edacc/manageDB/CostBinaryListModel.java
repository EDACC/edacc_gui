package edacc.manageDB;

import edacc.model.CostBinary;
import java.io.File;
import java.util.ArrayList;
import javax.swing.AbstractListModel;

/**
 *
 * @author simon
 */
public class CostBinaryListModel extends AbstractListModel {

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

    public CostBinaryListModel(CostBinary costBinary) {
        super();
        paths = new ArrayList<String>();
        for (File f : costBinary.getBinaryFiles()) {
            paths.addAll(findAllPaths(new File(costBinary.getRootDir()), new File(costBinary.getRootDir() + System.getProperty("file.separator") + f.getPath())));
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

    public int getIndexOf(String path) {
        return paths.indexOf(path);
    }
}
