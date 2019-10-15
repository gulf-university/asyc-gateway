package com.gulf.async.gateway.remoting.http.connection;

import com.gulf.async.gateway.remoting.api.connection.Connection;
import io.netty.channel.Channel;

/**
 * Created by xubai on 2019/10/07 4:55 PM.
 */
public class HttpConnection implements Connection<Channel> {

    private String channelId;

    private Channel channel;


    public HttpConnection(String channelId, Channel channel) {
        this.channelId = channelId;
        this.channel = channel;
    }

    @Override
    public String id() {
        return channelId;
    }

    @Override
    public Channel target() {
        return channel;
    }

    @Override
    public boolean alive() {
        return channel != null && channel.isActive();
    }
}
