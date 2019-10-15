package com.gulf.async.gateway.remoting.dubbo.utils;


import com.gulf.async.gateway.common.serialize.dubbo.buff.ChannelBuffer;
import com.gulf.async.gateway.common.serialize.dubbo.buff.ChannelBufferFactory;
import com.gulf.async.gateway.common.serialize.dubbo.buff.ChannelBuffers;

import java.nio.ByteBuffer;

/**
 * Wrap netty dynamic channel buffer.
 *
 * @author <a href="mailto:gang.lvg@taobao.com">kimi</a>
 */
public class NettyBackedChannelBufferFactory implements ChannelBufferFactory {

    private static final NettyBackedChannelBufferFactory INSTANCE = new NettyBackedChannelBufferFactory();

    public static ChannelBufferFactory getInstance() {
        return INSTANCE;
    }

    
    public ChannelBuffer getBuffer(int capacity) {
        return new NettyBackedChannelBuffer(ChannelBuffers.dynamicBuffer(capacity));
    }

    
    public ChannelBuffer getBuffer(byte[] array, int offset, int length) {
        ChannelBuffer buffer = ChannelBuffers.dynamicBuffer(length);
        buffer.writeBytes(array, offset, length);
        return new NettyBackedChannelBuffer(buffer);
    }

    
    public ChannelBuffer getBuffer(ByteBuffer nioBuffer) {
        return new NettyBackedChannelBuffer(ChannelBuffers.wrappedBuffer(nioBuffer));
    }
}
