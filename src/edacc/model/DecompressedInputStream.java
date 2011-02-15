package edacc.model;

import SevenZip.Compression.LZMA.Decoder;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.swing.SwingUtilities;

/**
 *
 * @author simon
 */
public class DecompressedInputStream extends InputStream {

    private long outSize;
    private Decoder dec;
    private InputStream input;

    long outPos = 0;
    int bufPos = 0;
    int curBufSize = 0;
    final static int maxBufSize = 256 * 1024;
    int[] buf = new int[maxBufSize * 2];

    public DecompressedInputStream(Decoder dec, long outSize, InputStream input) {
        this.dec = dec;
        this.outSize = outSize;
        this.input = input;
    }

    @Override
    public synchronized void reset() throws IOException {
        // this is a hack, possible error
        // TODO: fix
        bufPos = 0;
        outPos = 0;
    }

    @Override
    public int read() throws IOException {
        if (outPos >= outSize) {
            return -1;
        }
        if (bufPos >= curBufSize) {
            bufPos = 0;

            long bytesToRead = maxBufSize;
            if (outSize - outPos < bytesToRead) {
                bytesToRead = outSize - outPos;
            }

            dec.Code(input, new OutputStream() {

                @Override
                public void write(int b) throws IOException {

                    buf[bufPos++] = b;
                }
            }, outSize, bytesToRead, null);
            curBufSize = bufPos;
            bufPos = 0;
        }
        outPos++;
       /* if (outPos % 128 == 0) {
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    if (Tasks.getTaskView() != null) {
                        Tasks.getTaskView().setProgress2(outPos / (float) outSize);
                    }
                }
            });
        }*/
        return buf[bufPos++];
    }
}
