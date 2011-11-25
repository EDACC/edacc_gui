/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edacc.manageDB;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Enumeration;
import java.util.NoSuchElementException;

/**
 *
 * @author dgall
 */
public class FileInputStreamList implements Enumeration<FileInputStream> {

    private int currentElement;
    private File[] files;

    public FileInputStreamList(File[] files) {
        if (files.length > 0) {
            currentElement = 0;
            this.files = files;
        } else {
            currentElement = -1;
        }
    }

    @Override
    public FileInputStream nextElement() {
        if (hasMoreElements()) {
            File f = files[currentElement++];
            try {
                System.out.println("EXISTS: " + f.getAbsolutePath() + ": " + f.exists());
                return new FileInputStream(f);
            } catch (FileNotFoundException ex) {
                throw new NoSuchElementException("File " + f.getAbsolutePath() + " not found:\n" + ex.getMessage());
            }
        }
        throw new NoSuchElementException();
    }

    @Override
    public boolean hasMoreElements() {
        if (currentElement < 0)
            return false;
        if (currentElement >= files.length)
            return false;
        return true;
    }
}
