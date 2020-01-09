package com.gulf.async.gateway.remoting.dubbo.codec;

import com.gulf.async.gateway.remoting.spi.Codec;
import io.netty.channel.ChannelHandler;

/**
 * Created by xubai on 2019/10/11 6:03 PM.
 */
public class DubboCodec implements Codec {

    @Override
    public ChannelHandler encoder() {
        return new DubboEncoder();
    }

    @Override
    public ChannelHandler decoder() {
        return new DubboDecoder();
    }
}
