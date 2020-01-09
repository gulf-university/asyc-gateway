package com.gulf.async.gateway.remoting.dubbo.handler;

import com.gulf.async.gateway.common.log.Logger;
import com.gulf.async.gateway.common.log.LoggerFactory;
import com.gulf.async.gateway.remoting.spi.connection.ConnectionManager;
import com.gulf.async.gateway.remoting.spi.utils.RemotingUtil;
import com.gulf.async.gateway.remoting.dubbo.connection.DubboConnection;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

import java.net.SocketAddress;

/**
 * Created by xubai on 2019/10/15 3:30 PM.
 */
public class DubboConnectionHandler extends ChannelDuplexHandler {

    private final static Logger LOG = LoggerFactory.getInstance(DubboConnectionHandler.class);

    private ConnectionManager<DubboConnection> connectionManager;

    public DubboConnectionHandler(ConnectionManager<DubboConnection> connectionManager) {
        this.connectionManager = connectionManager;
    }

    public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress,
                        SocketAddress localAddress, ChannelPromise promise) throws Exception {
        final String local = localAddress == null ? "UNKNOW" : localAddress.toString();
        final String remote = remoteAddress == null ? "UNKNOW" : remoteAddress.toString();
        LOG.info("dubbo client pipeline: connect  {} => {}", local, remote);
        super.connect(ctx, remoteAddress, localAddress, promise);
    }

    public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        final String remoteAddress = RemotingUtil.parseRemoteAddress(ctx.channel());
        LOG.info("dubbo client pipeline: disconnect {}", remoteAddress);
        connectionManager.remove(remoteAddress);
        super.disconnect(ctx, promise);
    }

    public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        final String remoteAddress = RemotingUtil.parseRemoteAddress(ctx.channel());
        LOG.info("dubbo client pipeline: close {}", remoteAddress);
        connectionManager.remove(remoteAddress);
        super.close(ctx, promise);
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        final String remoteAddress = RemotingUtil.parseRemoteAddress(ctx.channel());
        LOG.warn("dubbo client pipeline:{} exceptionCaught exception.", remoteAddress, cause);
        connectionManager.remove(remoteAddress);
    }

    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent evnet = (IdleStateEvent) evt;
            if (evnet.state().equals(IdleState.ALL_IDLE)) {
                final String remoteAddress = RemotingUtil.parseRemoteAddress(ctx.channel());
                LOG.warn("dubbo client pipeline: idle exception [{}]", remoteAddress);
                connectionManager.remove(remoteAddress);
            }
        }
        ctx.fireUserEventTriggered(evt);
    }

}
