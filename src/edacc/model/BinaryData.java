package edacc.model;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

/**
 *
 * @author simon
 */
public class BinaryData implements Serializable {
    private byte[] data;
    
    
    public BinaryData(byte[] data) {
        this.data = data;
    }
    
    public BinaryData(InputStream stream) throws IOException {
        byte[] buffer = new byte[2048];
        int n;
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        while ((n = stream.read(buffer)) > 0) {
            os.write(buffer, 0, n);
        }
        data = os.toByteArray();
    }
    
    public byte[] getData() {
        return data;
    }
}
