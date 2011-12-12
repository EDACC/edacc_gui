/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edacc.manageDB;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.NoSuchElementException;

/**
 *
 * @author dgall
 */
public class FileInputStreamList implements Enumeration<FileInputStream> {

    private int currentElement;
    private LinkedList<File> files;

    public FileInputStreamList(File[] files) {
        if (files.length > 0) {
            currentElement = 0;
            this.files = new LinkedList<File>();
            this.files.addAll(Arrays.asList(files));
        } else {
            currentElement = -1;
        }
    }

    @Override
    public FileInputStream nextElement() {
        if (hasMoreElements()) {
            File f = files.get(currentElement++);
            try {
                if (f.isDirectory()) {
                    files.addAll(Arrays.asList(f.listFiles()));
                    return nextElement();
                }
                return new FileInputStream(f);
            } catch (FileNotFoundException ex) {
                throw new NoSuchElementException("File " + f.getAbsolutePath() + " not found:\n" + ex.getMessage());
            }
        }
        throw new NoSuchElementException();
    }

    @Override
    public boolean hasMoreElements() {
        if (currentElement < 0) {
            return false;
        }
        if (currentElement >= files.size()) {
            return false;
        }
        return true;
    }
}
