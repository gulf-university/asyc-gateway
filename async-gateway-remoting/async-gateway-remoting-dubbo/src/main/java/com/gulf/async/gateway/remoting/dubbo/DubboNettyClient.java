package com.gulf.async.gateway.remoting.dubbo;

import com.gulf.async.gateway.common.thread.NamedThreadFactory;
import com.gulf.async.gateway.common.util.SystemPropertyUtil;
import com.gulf.async.gateway.remoting.api.Codec;
import com.gulf.async.gateway.remoting.api.client.AbstractRemotingClient;
import com.gulf.async.gateway.remoting.api.config.NetworkConfigs;
import com.gulf.async.gateway.remoting.api.connection.ConnectionManager;
import com.gulf.async.gateway.remoting.api.context.RemotingContext;
import com.gulf.async.gateway.remoting.api.utils.RemotingUtil;
import com.gulf.async.gateway.remoting.dubbo.codec.DubboCodec;
import com.gulf.async.gateway.remoting.dubbo.connection.DubboConnection;
import com.gulf.async.gateway.remoting.dubbo.handler.DubboClientHandler;
import com.gulf.async.gateway.remoting.dubbo.handler.DubboConnectionHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;

/**
 * Created by xubai on 2019/10/11 3:30 PM.
 */
public class DubboNettyClient extends AbstractRemotingClient<ChannelFuture> {

    private Bootstrap bootstrap;

    private EventLoopGroup dubboIOWorker;

    private DefaultEventExecutorGroup dubboResponsePool;

    private Codec codec;

    private ConnectionManager<DubboConnection> connectionManager;
    private DubboConnectionHandler connectionHandler;

    public DubboNettyClient(ConnectionManager<DubboConnection> connectionManager) {
        this.connectionManager = connectionManager;
    }

    @Override
    protected void initConfigs() {
        config(NetworkConfigs.NETTY_EPOLL_SWITCH).setIfAbsent(NetworkConfigs.NETTY_EPOLL_SWICH_DEFAULT);
        config(NetworkConfigs.TCP_NODELAY).setIfAbsent(NetworkConfigs.TCP_NODELAY_DEFAULT);
        config(NetworkConfigs.SO_KEEPALIVE).setIfAbsent(NetworkConfigs.TCP_SO_KEEPALIVE_DEFAULT);
        config(NetworkConfigs.SO_SNDBUF).setIfAbsent(NetworkConfigs.SO_SNDBUF_DEFAULT);
        config(NetworkConfigs.SO_RCVBUF).setIfAbsent(NetworkConfigs.SO_RCVBUF_DEFAULT);

        Integer responsePoolSize = SystemPropertyUtil.getInt("gateway.response_pool_size", Math.max(Runtime.getRuntime().availableProcessors() * 2 +1, 4));
        dubboResponsePool = new DefaultEventExecutorGroup(responsePoolSize, new NamedThreadFactory("gateway-dubbo-response-pool"));

        if (config(NetworkConfigs.NETTY_EPOLL_SWITCH).get() && Epoll.isAvailable()){
            this.dubboIOWorker = new EpollEventLoopGroup(2, new NamedThreadFactory("gateway-dubbo-io-pool"));
        }else{
            this.dubboIOWorker = new NioEventLoopGroup(2, new NamedThreadFactory("gateway-dubbo-io-pool"));
        }

        codec = new DubboCodec();
        connectionHandler = new DubboConnectionHandler(connectionManager);
        this.bootstrap.group(this.dubboIOWorker)
                .channel(NioSocketChannel.class)//
                .option(ChannelOption.TCP_NODELAY, config(NetworkConfigs.TCP_NODELAY).get())
                .option(ChannelOption.SO_KEEPALIVE, config(NetworkConfigs.SO_KEEPALIVE).get())
                .option(ChannelOption.SO_SNDBUF, config(NetworkConfigs.SO_SNDBUF).get())
                .option(ChannelOption.SO_RCVBUF, config(NetworkConfigs.SO_RCVBUF).get())
                .handler(new ChannelInitializer<SocketChannel>() {

                    public void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(//
                                dubboResponsePool, //
                                codec.encoder(), //
                                codec.decoder(), //
                                new IdleStateHandler(0, 0, SystemPropertyUtil.getInt("gateway.dubbo_client_max_idle_seconds", 120)),//
                                connectionHandler, //
                                new DubboClientHandler()
                        );
                    }
                });
    }

    @Override
    public ChannelFuture connect(String url) {
        return this.bootstrap.connect(RemotingUtil.string2SocketAddress(url));
    }

    @Override
    protected void disconnect() {
        dubboResponsePool.shutdownGracefully();
        dubboIOWorker.shutdownGracefully();
    }

}