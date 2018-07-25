package group.rober.runtime.io;

import java.io.OutputStream;

/**
 * 二进制输出流
 */
public class ByteOutputStream extends OutputStream {
    /**
     * Buffer and size
     */
    protected byte[] buf = null;
    protected int size = 0;

    /**
     * Constructs a stream with buffer capacity size 5K
     */
    public ByteOutputStream() {
        this(5 * 1024);
    }

    /**
     * Constructs a stream with the given initial size
     *
     * @param initSize  initSize
     */
    public ByteOutputStream(int initSize) {
        this.size = 0;
        this.buf = new byte[initSize];
    }

    /**
     * Ensures that we have a large enough buffer for the given size.
     */
    private void verifyBufferSize(int sz) {
        if (sz > buf.length) {
            byte[] old = buf;
            buf = new byte[Math.max(sz, 2 * buf.length)];
            System.arraycopy(old, 0, buf, 0, old.length);
            old = null;
        }
    }

    public int getSize() {
        return size;
    }

    public final void write(byte b[]) {
        verifyBufferSize(size + b.length);
        System.arraycopy(b, 0, buf, size, b.length);
        size += b.length;
    }

    public final void write(byte b[], int off, int len) {
        verifyBufferSize(size + len);
        System.arraycopy(b, off, buf, size, len);
        size += len;
    }

    public final void write(int b) {
        verifyBufferSize(size + 1);
        buf[size++] = (byte) b;
    }

    public void reset() {
        size = 0;
    }

    public ByteInputStream createInputStream() {
        return new ByteInputStream(buf, size);
    }


    public final byte[] toBytes() {
        byte newbuf[] = new byte[size];
        System.arraycopy(buf, 0, newbuf, 0, size);
        return newbuf;
    }
}
