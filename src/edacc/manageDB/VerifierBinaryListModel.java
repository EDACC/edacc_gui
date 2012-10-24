package edacc.manageDB;

import java.io.File;
import java.util.ArrayList;
import javax.swing.AbstractListModel;

/**
 *
 * @author simon
 */
    public class VerifierBinaryListModel extends AbstractListModel {

    private ArrayList<String> paths;

    private ArrayList<String> findAllPaths(File base, File f) {
        ArrayList<String> res = new ArrayList<String>();
        if (f.isDirectory()) {
            for (File file : f.listFiles()) {
                res.addAll(findAllPaths(base, file));
            }
        } else {
            String path = f.getPath().replace(base.getPath(), "");
            if (path.startsWith(System.getProperty("file.separator"))) {
                path = path.substring(1);
            }
            path = path.replace(System.getProperty("file.separator"), "/");
            res.add(path);
        }
        return res;
    }

    public VerifierBinaryListModel(File[] files) {
        super();
        paths = new ArrayList<String>();
        for (File f : files) {
            if (f.isDirectory()) {
                paths.addAll(findAllPaths(f.getParentFile(), f));
            } else {
                paths.add(f.getName());
            }
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
