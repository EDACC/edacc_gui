/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edacc.manageDB;

import SevenZip.CoderPropID;
import SevenZip.Compression.LZMA.Decoder;
import SevenZip.ICodeProgress;
import edacc.model.CostBinary;
import edacc.model.DecompressedInputStream;
import edacc.model.ExperimentDAO;
import edacc.model.GridQueueDAO;
import edacc.model.InstanceClassDAO;
import edacc.model.InstanceDAO;
import edacc.model.InstanceHasPropertyDAO;
import edacc.model.ParameterDAO;
import edacc.model.ParameterInstanceDAO;
import edacc.model.PropertyDAO;
import edacc.model.SolverBinaries;
import edacc.model.SolverDAO;
import edacc.model.Verifier;
import edacc.model.VerifierParameter;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 *
 * @author dgall
 */
public class Util {

    private static final String[] constVerifierParameters = {"instance", "output_solver", "output_launcher", "output_watcher"};

    /**
     * Calculates the MD5 sum of the given file.
     * @param file
     * @return the MD5 sum representing the given file.
     * @throws FileNotFoundException
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    public static String calculateMD5(File file) throws FileNotFoundException, IOException, NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("MD5");
        InputStream is = new FileInputStream(file);
        byte[] buffer = new byte[8192];
        int read = 0;
        while ((read = is.read(buffer)) > 0) {
            digest.update(buffer, 0, read);
        }
        is.close();
        byte[] md5sum = digest.digest();
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < md5sum.length; i++) {
            int halfbyte = (md5sum[i] >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                if ((0 <= halfbyte) && (halfbyte <= 9)) {
                    buf.append((char) ('0' + halfbyte));
                } else {
                    buf.append((char) ('a' + (halfbyte - 10)));
                }
                halfbyte = md5sum[i] & 0x0F;
            } while (two_halfs++ < 1);
        }
        String res = buf.toString();
        return buf.toString();
    }

    public static String calculateMD5(InputStream is) throws NoSuchAlgorithmException, IOException {
        MessageDigest digest = MessageDigest.getInstance("MD5");
        byte[] buffer = new byte[8192];
        int read = 0;
        while ((read = is.read(buffer)) > 0) {
            digest.update(buffer, 0, read);
        }
        byte[] md5sum = digest.digest();
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < md5sum.length; i++) {
            int halfbyte = (md5sum[i] >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                if ((0 <= halfbyte) && (halfbyte <= 9)) {
                    buf.append((char) ('0' + halfbyte));
                } else {
                    buf.append((char) ('a' + (halfbyte - 10)));
                }
                halfbyte = md5sum[i] & 0x0F;
            } while (two_halfs++ < 1);
        }
        String res = buf.toString();
        return buf.toString();
    }

    /**
     * Calculates the MD5 sum of the given String
     * @param formula
     * @return the MD5 sum representing the given String
     * @throws FileNotFoundException
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    public static String calculateMD5(String formula) throws FileNotFoundException, IOException, NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("MD5");
        byte[] buffer = formula.getBytes();
        digest.update(buffer);
        byte[] md5sum = digest.digest();
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < md5sum.length; i++) {
            int halfbyte = (md5sum[i] >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                if ((0 <= halfbyte) && (halfbyte <= 9)) {
                    buf.append((char) ('0' + halfbyte));
                } else {
                    buf.append((char) ('a' + (halfbyte - 10)));
                }
                halfbyte = md5sum[i] & 0x0F;
            } while (two_halfs++ < 1);
        }
        String res = buf.toString();
        return buf.toString();
    }

    public static ByteArrayOutputStream zipDirectoryToByteStream(File dir) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ZipOutputStream out = new ZipOutputStream(bos);
        zip(dir, dir, out);
        out.close();
        return bos;
    }

    public static ByteArrayOutputStream zipFileArrayToByteStream(File[] files) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ZipOutputStream out = new ZipOutputStream(bos);
        zip(files, out);
        out.close();
        return bos;
    }

    public static ByteArrayOutputStream zipFileArrayToByteStream(File[] files, File base) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ZipOutputStream out = new ZipOutputStream(bos);
        zip(files, base, out);
        out.close();
        return bos;
    }

    public static ByteArrayOutputStream zipFileArrayToByteStreamAutoBasePath(File[] files) throws FileNotFoundException, IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ZipOutputStream out = new ZipOutputStream(bos);
        for (File f : files) {
            if (f.isDirectory()) {
                zipAutoBase(f, f.getParentFile(), out);
            } else {
                FileInputStream fin = new FileInputStream(f);
                ZipEntry entry = new ZipEntry(f.getName());
                out.putNextEntry(entry);
                byte[] buffer = new byte[8192];
                int bytes_read = 0;
                while ((bytes_read = fin.read(buffer)) != -1) {
                    out.write(buffer, 0, bytes_read);
                }
                fin.close();
            }
        }
        out.close();
        return bos;
    }

    private static void zipAutoBase(File f, File base, ZipOutputStream out) throws FileNotFoundException, IOException {
        if (f.isDirectory()) {
            for (File file : f.listFiles()) {
                zipAutoBase(file, base, out);
            }
        } else {
            String path = f.getPath().replace(base.getPath(), "");
            if (path.startsWith(System.getProperty("file.separator"))) {
                path = path.substring(1);
            }
            path = path.replace(System.getProperty("file.separator"), "/");
            FileInputStream fin = new FileInputStream(f);
            ZipEntry entry = new ZipEntry(path);
            out.putNextEntry(entry);
            byte[] buffer = new byte[8192];
            int bytes_read = 0;
            while ((bytes_read = fin.read(buffer)) != -1) {
                out.write(buffer, 0, bytes_read);
            }
            fin.close();
        }
    }

    private static void zip(File dir, File base, ZipOutputStream out) throws IOException {
        File[] files = dir.listFiles();
        byte[] buffer = new byte[8192];
        final String FILE_SEP = System.getProperty("file.separator");

        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                zip(files[i], base, out); // recursively zip sub-directories
            } else {
                FileInputStream fin = new FileInputStream(files[i]);
                ZipEntry entry = new ZipEntry(files[i].getPath().substring(base.getPath().length() + 1).replace(FILE_SEP, "/"));
                out.putNextEntry(entry);

                int bytes_read = 0;
                while ((bytes_read = fin.read(buffer)) != -1) {
                    out.write(buffer, 0, bytes_read);
                }
                fin.close();
            }
        }
    }

    private static void zip(File[] files, ZipOutputStream out) throws IOException {
        byte[] buffer = new byte[8192];

        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                zip(files[i], files[i], out); // recursively zip sub-directories
            } else {
                FileInputStream fin = new FileInputStream(files[i]);
                ZipEntry entry = new ZipEntry(files[i].getPath());
                out.putNextEntry(entry);

                int bytes_read = 0;
                while ((bytes_read = fin.read(buffer)) != -1) {
                    out.write(buffer, 0, bytes_read);
                }
                fin.close();
            }
        }
    }

    private static void zip(File[] files, File base, ZipOutputStream out) throws IOException {
        byte[] buffer = new byte[8192];
        final String FILE_SEP = System.getProperty("file.separator");

        for (int i = 0; i < files.length; i++) {
            File file_on_disk = new File(base.getPath() + FILE_SEP + files[i].getPath());
            if (file_on_disk.isDirectory()) {
                zip(file_on_disk, base, out); // recursively zip sub-directories
            } else {
                FileInputStream fin = new FileInputStream(file_on_disk);
                ZipEntry entry = new ZipEntry(files[i].getPath().replace(FILE_SEP, "/"));
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
        ParameterDAO.clearCache();
        PropertyDAO.clearCache();
        InstanceHasPropertyDAO.clearCache();
    }

    /* public static void sevenZipEncode(File input, File output) throws FileNotFoundException, Exception {
    java.io.BufferedInputStream inStream = new java.io.BufferedInputStream(new java.io.FileInputStream(input));
    java.io.BufferedOutputStream outStream = new java.io.BufferedOutputStream(new java.io.FileOutputStream(output.getAbsolutePath()));
    Encoder encoder = new Encoder();
    if (!encoder.SetAlgorithm(-1)) {
    throw new Exception("Incorrect compression mode");
    }
    if (!encoder.SetDictionarySize(23)) {
    throw new Exception("Incorrect dictionary size");
    }
    if (!encoder.SetNumFastBytes(128)) {
    throw new Exception("Incorrect -fb value");
    }
    if (!encoder.SetMatchFinder(1)) {
    throw new Exception("Incorrect -mf value");
    }
    if (!encoder.SetLcLpPb(3, 0, 2)) {
    throw new Exception("Incorrect -lc or -lp or -pb value");
    }
    encoder.SetEndMarkerMode(false);
    encoder.WriteCoderProperties(outStream);
    
    long fileSize = input.length();
    for (int i = 0; i < 8; i++) {
    outStream.write((int) (fileSize >>> (8 * i)) & 0xFF);
    }
    encoder.Code(inStream, outStream, -1, -1, null);
    outStream.flush();
    outStream.close();
    inStream.close();
    }*/

    /*  public static void sevenZipDecode(File input, File f) throws FileNotFoundException, IOException, Exception {
    
    // Decode the instance file
    java.io.BufferedInputStream inStream = new java.io.BufferedInputStream(new java.io.FileInputStream(input));
    java.io.BufferedOutputStream outStream = new java.io.BufferedOutputStream(new java.io.FileOutputStream(f));
    int propertiesSize = 5;
    byte[] properties = new byte[propertiesSize];
    if (inStream.read(properties, 0, propertiesSize) != propertiesSize) {
    throw new Exception("input .lzma file is too short");
    }
    Decoder decoder = new Decoder();
    if (!decoder.SetDecoderProperties(properties)) {
    throw new Exception("Incorrect stream properties");
    }
    long outSize = 0;
    for (int j = 0; j < 8; j++) {
    int v = inStream.read();
    outSize |= ((long) v) << (8 * j);
    }
    if (!decoder.Code(inStream, outStream, outSize))
    throw new Exception("Error in data stream");
    outStream.flush();
    outStream.close();
    inStream.close();
    }*/
    public static final String lzma_identifier = "LZMA";

    public static void sevenZipEncode(InputStream is, OutputStream os, final long size, ICodeProgress progr) throws IOException {
        SevenZip.Compression.LZMA.Encoder enc = new SevenZip.Compression.LZMA.Encoder();
        enc.SetCoderProperties(new int[]{CoderPropID.Algorithm, CoderPropID.DictionarySize, CoderPropID.MatchFinder, CoderPropID.LitContextBits, CoderPropID.LitPosBits, CoderPropID.PosStateBits, CoderPropID.NumFastBytes}, new int[]{0, 1 << 8, enc.EMatchFinderType_BT4B, 3, 0, 2, 128});
        for (int i = 0; i < lzma_identifier.length(); i++) {
            os.write((byte) lzma_identifier.charAt(i));
        }

        enc.WriteCoderProperties(os);
        for (int i = 0; i < 8; i++) {
            os.write((byte) (size >> (8 * i)));
        }

        enc.Code(is, os, progr);
        is.close();
        os.close();
    }

    private static Decoder getDecoder(InputStream is) throws IOException {
        byte[] properties = new byte[5];
        if (is.read(properties, 0, 5) != 5) {
            throw (new IOException("input .lzma is too short"));
        }
        SevenZip.Compression.LZMA.Decoder dec = new SevenZip.Compression.LZMA.Decoder();
        dec.SetDecoderProperties(properties);

        return dec;
    }

    private static long getOutSize(InputStream is) throws IOException {
        long outSize = 0;
        for (int i = 0; i < 8; i++) {
            int v = is.read();
            if (v < 0) {
                throw (new IOException("Can't Read 1"));
            }
            outSize |= ((long) v) << (8 * i);
        }
        if (outSize == -1) {
            outSize = Long.MAX_VALUE;
        }
        return outSize;
    }

    public static void sevenZipDecode(InputStream is, OutputStream os) throws IOException {
        if (isLZMA(is)) {
            Decoder dec = getDecoder(is);
            long outSize = getOutSize(is);
            dec.Code(is, os, outSize, 0, null);
            is.close();
            os.close();
        } else {
            byte[] buffer = new byte[1024 * 1024];
            int len;
            while ((len = is.read(buffer)) > 0) {
                os.write(buffer, 0, len);
            }
        }
    }

    public static boolean isLZMA(InputStream is) throws IOException {
        byte[] buf = new byte[lzma_identifier.length()];
        is.mark(lzma_identifier.length() + 8);
        if (is.read(buf, 0, lzma_identifier.length()) != lzma_identifier.length()) {
            is.reset();
            return false;
        }
        for (int i = 0; i < lzma_identifier.length(); i++) {
            if (buf[i] != lzma_identifier.charAt(i)) {
                is.reset();
                return false;
            }
        }
        return true;
    }
    
    public static InputStream getDecompressedInputStream(final InputStream input) throws IOException {
        return getDecompressedInputStream(input, true);
    }
    
    public static InputStream getDecompressedInputStream(final InputStream input, boolean useTasks) throws IOException {
        if (isLZMA(input)) {
            final Decoder dec = getDecoder(input);
            final long outSize = getOutSize(input);
            return new DecompressedInputStream(dec, outSize, input, useTasks);
        } else {
            return input;
        }
    }

    public static void removeCommonPrefix(SolverBinaries b) {
        File[] files = b.getBinaryFiles();
        String lcp = getCommonPrefix(files);
        b.setRootDir(lcp);
        for (int i = 0; i < files.length; i++) {
            files[i] = new File(files[i].getAbsolutePath().replace(lcp, ""));
        }
    }

    public static void removeCommonPrefix(CostBinary b) {
        File[] files = b.getBinaryFiles();
        String lcp = getCommonPrefix(files);
        b.setRootDir(lcp);
        for (int i = 0; i < files.length; i++) {
            files[i] = new File(files[i].getAbsolutePath().replace(lcp, ""));
        }
    }

    /**
     * Removes the common prefix of a file array and returns the prefix.
     * @param files
     * @return
     */
    public static String removeCommonPrefix(File[] files) {
        String lcp = getCommonPrefix(files);
        for (int i = 0; i < files.length; i++) {
            files[i] = new File(files[i].getAbsolutePath().replace(lcp, ""));
        }
        return lcp;
    }

    private static String getCommonPrefix(File[] files) {
        final String separator = System.getProperty("file.separator");
        if (files == null || files.length == 0) {
            return "";
        }
        Arrays.sort(files, new Comparator<File>() {

            @Override
            public int compare(File o1, File o2) {
                return o1.getPath().compareTo(o2.getPath());
            }
        });
        String lcp = "";
        for (int j = 1; j < files[0].getPath().length(); j++) {
            String pref = files[0].getPath().substring(0, j);
            for (int i = 1; i < files.length; i++) {
                if (!files[i].getPath().startsWith(pref)) {
                    return lcp;
                }
            }
            if (pref.length() - separator.length() >= 0 && pref.substring(pref.length() - separator.length(), pref.length()).equals(separator)) {
                lcp = pref;
            }
        }
        return lcp;
    }

    /**
     * Transforms the parameters from this verifier to a string.
     * @param verifier the verifier
     * @return null if an error occurred
     */
    public static String getVerifierParameterString(Verifier verifier) {
        try {
            if (verifier.getParameters().isEmpty()) {
                return "";
            }
            List<VerifierParameter> params = new ArrayList<VerifierParameter>();
            params.addAll(verifier.getParameters());
            String paramString = "";
            Collections.sort(params, new Comparator<VerifierParameter>() {

                @Override
                public int compare(VerifierParameter o1, VerifierParameter o2) {
                    if (o1.getOrder() > o2.getOrder()) {
                        return 1;
                    } else if (o1.getOrder() == o2.getOrder()) {
                        return 0;
                    } else {
                        return -1;
                    }
                }
            });
            for (VerifierParameter param : params) {
                boolean found = false;
                for (String s : constVerifierParameters) {
                    if (s.equals(param.getName().toLowerCase())) {
                        paramString += param.getPrefix() == null ? "<" + s + ">" : (param.getPrefix() + (param.getSpace() ? " " : "")) + "<" + s + ">";
                        found = true;
                        break;
                    }
                }
                if (found) {
                } else if (param.getHasValue()) {
                    paramString += param.getPrefix() == null ? "<value>" : (param.getPrefix() + (param.getSpace() ? " " : "")) + "<value>";
                } else {
                    paramString += param.getPrefix() == null ? "" : (param.getPrefix());
                }

                if (params.get(params.size() - 1) != param) {
                    paramString += " ";
                }
            }
            return paramString;
        } catch (Exception e) {
            return null;
        }
    }
}
