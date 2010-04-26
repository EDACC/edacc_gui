/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edacc.manageDB;

import edacc.model.ExperimentDAO;
import edacc.model.GridQueueDAO;
import edacc.model.InstanceClassDAO;
import edacc.model.InstanceDAO;
import edacc.model.ParameterInstanceDAO;
import edacc.model.SolverDAO;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 *
 * @author dgall
 */
public class Util {

    public static String calculateMD5(File file) throws FileNotFoundException, IOException, NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("MD5");
        InputStream is = new FileInputStream(file);
        byte[] buffer = new byte[8192];
        int read = 0;
        while ((read = is.read(buffer)) > 0) {
            digest.update(buffer, 0, read);
        }
        byte[] md5sum = digest.digest();
        BigInteger bigInt = new BigInteger(1, md5sum);
        return bigInt.toString(16);
    }

    public static ByteArrayOutputStream zipDirectoryToByteStream(File dir) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ZipOutputStream out = new ZipOutputStream(bos);
        zip(dir, dir, out);
        out.close();
        return bos;
    }

    private static void zip(File dir, File base, ZipOutputStream out) throws IOException {
        File[] files = dir.listFiles();
        byte[] buffer = new byte[8192];

        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                zip(files[i], base, out); // recursively zip sub-directories
            } else {
                FileInputStream fin = new FileInputStream(files[i]);
                ZipEntry entry = new ZipEntry(files[i].getPath().substring(base.getPath().length() + 1));
                out.putNextEntry(entry);

                int bytes_read = 0;
                while ((bytes_read = fin.read(buffer)) != -1) {
                    out.write(buffer, 0, bytes_read);
                }
                fin.close();
            }
        }
    }

    public static void unzip(File zip, File extractTo) throws IOException {
        ZipFile archive = new ZipFile(zip);
        Enumeration e = archive.entries();
        while (e.hasMoreElements()) {
            ZipEntry entry = (ZipEntry) e.nextElement();
            File file = new File(extractTo, entry.getName());
            if (entry.isDirectory() && !file.exists()) {
                file.mkdirs();
            } else {
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }

                InputStream in = archive.getInputStream(entry);
                BufferedOutputStream out = new BufferedOutputStream(
                        new FileOutputStream(file));

                byte[] buffer = new byte[8192];
                int read;

                while (-1 != (read = in.read(buffer))) {
                    out.write(buffer, 0, read);
                }

                in.close();
                out.close();
            }
        }
    }

    public static void clearCaches() {
        ExperimentDAO.clearCache();
        GridQueueDAO.clearCache();
        InstanceDAO.clearCache();
        InstanceClassDAO.clearCache();
        SolverDAO.clearCache();
        ParameterInstanceDAO.clearCache();
    }
}

