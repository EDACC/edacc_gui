package edacc.model;

import edacc.experiment.Util;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

/**
 *
 * @author simon
 */
public class FileCache {

    private String path;
    private static FileCache instance;
    private HashMap<FileIdentifier, File> cache;

    public static synchronized FileCache getInstance() {
        if (instance == null) {
            instance = new FileCache();
        }
        return instance;
    }

    private FileCache() {
        cache = new HashMap<FileIdentifier, File>();
        path = Util.getPath() + System.getProperty("file.separator") + "tmp" + System.getProperty("file.separator") + "FileCache" + System.getProperty("file.separator");
        File p = new File(path);
        p.mkdirs();
    }

    public synchronized boolean hasFile(int db_id, String md5) throws InterruptedException {
        FileIdentifier id = new FileIdentifier(db_id, md5);
        if (cache.containsKey(id)) {
            while (cache.get(id) == null) {
                //System.out.println(Thread.currentThread().getId() + " waiting for download");
                this.wait();
                //System.out.println(Thread.currentThread().getId() + " download ready");
            }
            //System.out.println("Cache hit");
            return true;
        } else {

            File f = new File(path + db_id + "_" + md5);
            if (f.exists()) {
                String md5_check = null;
                try {
                    InputStream is = new FileInputStream(f);
                    InputStream ucis = edacc.manageDB.Util.getDecompressedInputStream(is);
                    md5_check = edacc.manageDB.Util.calculateMD5(ucis);
                    ucis.close();
                    is.close();
                } catch (Exception e) {
                    md5_check = null;
                }
                if (md5.equals(md5_check)) {
                    cache.put(new FileIdentifier(db_id, md5), f);
                }
                //System.out.println("Cache hit");
                return true;
            }

            cache.put(new FileIdentifier(db_id, md5), null);
            //System.out.println("Cache miss");
            return false;
        }
    }

    public synchronized void cacheFile(InputStream is, int db_id, String md5) throws IOException {
        File f = new File(path + db_id + "_" + md5);
        //System.out.println("Cache file: " + path + db_id + "_" + md5);
        if (f.exists()) {
            // todo: error?
            //System.out.println("Warning file exists");
        }
        //f.createNewFile();
        FileOutputStream os = new FileOutputStream(f);
        byte[] buffer = new byte[16 * 1024];
        int n;
        while ((n = is.read(buffer)) > 0) {
            os.write(buffer, 0, n);
        }
        os.close();
        cache.put(new FileIdentifier(db_id, md5), f);
        this.notifyAll();
    }

    public synchronized InputStream getInputStream(int db_id, String md5) throws FileNotFoundException {
        File f = cache.get(new FileIdentifier(db_id, md5));
        if (f == null) {
            return null;
        }
        return new FileInputStream(f);
    }

    private class FileIdentifier {

        private int db_id;
        private String md5;

        public FileIdentifier(int db_id, String md5) {
            this.db_id = db_id;
            this.md5 = md5;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final FileIdentifier other = (FileIdentifier) obj;
            if (this.db_id != other.db_id) {
                return false;
            }
            if ((this.md5 == null) ? (other.md5 != null) : !this.md5.equals(other.md5)) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 53 * hash + this.db_id;
            hash = 53 * hash + (this.md5 != null ? this.md5.hashCode() : 0);
            return hash;
        }
    }
}
