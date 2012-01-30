package edacc.model;

import SevenZip.Compression.LZMA.Decoder;
import edacc.EDACCTaskView;
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
    private int id;
    private EDACCTaskView view;
    long outPos = 0;
    int bufPos = 0;
    int curBufSize = 0;
    final static int maxBufSize = 16 * 1024;
    byte[] buf = new byte[maxBufSize * 2];

    public DecompressedInputStream(Decoder dec, long outSize, InputStream input) {
        this.dec = dec;
        this.outSize = outSize;
        this.input = input;
        this.view = Tasks.getTaskView();
        if (view != null) {
            id = view.getSubTaskId();
            view.setMessage(id, "Decompressing");
        }
    }

    @Override
    public synchronized void reset() throws IOException {
        // this is a hack, possible error
        // TODO: fix
        bufPos = 0;
        outPos = 0;
    }

    private void fillBuffer() throws IOException {
        bufPos = 0;

        long bytesToRead = maxBufSize;
        if (outSize - outPos < bytesToRead) {
            bytesToRead = outSize - outPos;
        }

        dec.Code(input, new OutputStream() {

            @Override
            public void write(byte[] b, int off, int len) throws IOException {
                System.arraycopy(b, off, buf, bufPos, len);
                bufPos += len;
            }

            @Override
            public void write(int b) throws IOException {
                buf[bufPos++] = (byte) b;
            }
        }, outSize, bytesToRead, null);
        curBufSize = bufPos;
        bufPos = 0;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if (outPos >= outSize) {
            if (view != null) {
                view.subTaskFinished(id);
                view = null;
            }
            return -1;
        }
        if (bufPos >= curBufSize) {
            fillBuffer();
        }
        if (len > curBufSize - bufPos)
            len = curBufSize - bufPos;
        if (len > outSize - outPos)
            len = (int) (outSize - outPos);
        System.arraycopy(buf, bufPos, b, off, len);
        bufPos += len;
        outPos += len;
        view.setProgress(id, outPos / (float) outSize * 100);
        return len;
    }

    @Override
    public int read() throws IOException {
        if (outPos >= outSize) {
            if (view != null) {
                view.subTaskFinished(id);
                view = null;
            }
            return -1;
        }
        if (bufPos >= curBufSize) {
            fillBuffer();
        }
        outPos++;
        if (outPos % (32 * 1024) == 0 && view != null) {
            view.setProgress(id, outPos / (float) outSize * 100);
        }
        return buf[bufPos++];
    }

    @Override
    public void close() throws IOException {
        super.close();
        if (view != null) {
            view.subTaskFinished(id);
            view = null;
        }
    }
}
