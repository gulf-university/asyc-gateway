package com.gulf.async.gateway.remoting.spi;

import io.netty.channel.ChannelHandler;

/**
 * Created by xubai on 2019/09/19 5:38 PM.
 */
public interface Codec {

    //编码器
    ChannelHandler encoder();

    //编码器
    ChannelHandler decoder();

}
