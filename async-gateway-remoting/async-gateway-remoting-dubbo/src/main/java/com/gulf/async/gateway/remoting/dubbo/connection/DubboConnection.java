package com.gulf.async.gateway.remoting.dubbo.connection;

import com.gulf.async.gateway.remoting.api.connection.Connection;
import io.netty.channel.ChannelFuture;

/**
 * Created by xubai on 2019/10/15 3:33 PM.
 */
public class DubboConnection implements Connection<ChannelFuture> {

    private String remoteAddress;

    private ChannelFuture channelFuture;

    public DubboConnection(String remoteAddress, ChannelFuture channelFuture) {
        this.remoteAddress = remoteAddress;
        this.channelFuture = channelFuture;
    }

    @Override
    public String id() {
        return remoteAddress;
    }

    @Override
    public ChannelFuture target() {
        return channelFuture;
    }

    @Override
    public boolean alive() {
        return (this.channelFuture.channel() != null && this.channelFuture.channel().isActive());
    }
}
