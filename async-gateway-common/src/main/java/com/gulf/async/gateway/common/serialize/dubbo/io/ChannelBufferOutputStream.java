package com.gulf.async.gateway.common.serialize.dubbo.io;


import com.gulf.async.gateway.common.serialize.dubbo.buff.ChannelBuffer;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by xubai on 2018/11/07 5:30 PM.
  */
public class ChannelBufferOutputStream extends OutputStream {
    private final ChannelBuffer buffer;
    private final int           startIndex;

    public ChannelBufferOutputStream(ChannelBuffer buffer) {
        if (buffer == null) {
            throw new NullPointerException("buffer");
        }
        this.buffer = buffer;
        startIndex = buffer.writerIndex();
    }

    public int writtenBytes() {
        return buffer.writerIndex() - startIndex;
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        if (len == 0) {
            return;
        }

        buffer.writeBytes(b, off, len);
    }

    @Override
    public void write(byte[] b) throws IOException {
        buffer.writeBytes(b);
    }

    @Override
    public void write(int b) throws IOException {
        buffer.writeByte((byte) b);
    }

    public ChannelBuffer buffer() {
        return buffer;
    }
}
