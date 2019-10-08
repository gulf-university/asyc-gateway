package com.gulf.async.gateway.remoting.http.handler;

import com.gulf.async.gateway.common.log.Logger;
import com.gulf.async.gateway.common.log.LoggerFactory;
import com.gulf.async.gateway.remoting.api.connection.ConnectionManager;
import com.gulf.async.gateway.remoting.api.utils.RemotingUtil;
import com.gulf.async.gateway.remoting.http.connection.HttpConnection;
import com.gulf.async.gateway.remoting.http.connection.HttpConnectionManager;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * Created by xubai on 2019/09/30 4:57 PM.
 */
@ChannelHandler.Sharable
public class HttpConnectionHandler extends ChannelDuplexHandler {

    private final static Logger LOG = LoggerFactory.getInstance(HttpConnectionHandler.class);

    private ConnectionManager httpConnectionManager;

    public HttpConnectionHandler(ConnectionManager httpConnectionManager) {
        this.httpConnectionManager = httpConnectionManager;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent){
            try {
                //TODO 超时关闭需要特殊处理，否则报http 502
                ctx.close();
                LOG.warn("http Connection idle, close it from server side: {}", RemotingUtil.parseRemoteAddress(ctx.channel()));
            } catch (Exception e) {
                LOG.warn("Exception caught when closing http connection.", e);
            }
        }else{
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        httpConnectionManager.add(new HttpConnection(channel.id().asLongText(), channel));
        super.channelActive(ctx);
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        httpConnectionManager.add(new HttpConnection(channel.id().asLongText(), channel));
        super.channelRegistered(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        httpConnectionManager.remove(channel.id().asLongText());
        super.channelInactive(ctx);
    }
}
