package com.gulf.async.gateway.remoting.dubbo;

import com.gulf.async.gateway.common.thread.NamedThreadFactory;
import com.gulf.async.gateway.common.util.SystemPropertyUtil;
import com.gulf.async.gateway.remoting.spi.Codec;
import com.gulf.async.gateway.remoting.spi.client.AbstractRemotingClient;
import com.gulf.async.gateway.remoting.spi.config.NetworkConfigs;
import com.gulf.async.gateway.remoting.spi.connection.ConnectionManager;
import com.gulf.async.gateway.remoting.spi.utils.RemotingUtil;
import com.gulf.async.gateway.remoting.dubbo.codec.DubboCodec;
import com.gulf.async.gateway.remoting.dubbo.connection.DubboConnection;
import com.gulf.async.gateway.remoting.dubbo.handler.DubboClientHandler;
import com.gulf.async.gateway.remoting.dubbo.handler.DubboConnectionHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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

    private ThreadPoolExecutor executorService = null;

    private ChannelHandler dubboClientHandler;

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
        config(NetworkConfigs.WRITE_BUFFER_HIGH_WATER_MARK).setIfAbsent(NetworkConfigs.NETTY_BUFFER_HIGH_WATERMARK_DEFAULT);
        config(NetworkConfigs.WRITE_BUFFER_LOW_WATER_MARK).setIfAbsent(NetworkConfigs.NETTY_BUFFER_LOW_WATERMARK_DEFAULT);
        config(NetworkConfigs.CONNECT_TIMEOUT_MILLIS).setIfAbsent(NetworkConfigs.DEFAULT_CONNECT_TIMEOUT_MILLIS);

        Integer responsePoolSize = SystemPropertyUtil.getInt("gateway.response_pool_size", Math.max(Runtime.getRuntime().availableProcessors() * 3 +1, 9));
        dubboResponsePool = new DefaultEventExecutorGroup(responsePoolSize, new NamedThreadFactory("gateway-dubbo-response-pool"));

        if (config(NetworkConfigs.NETTY_EPOLL_SWITCH).get() && Epoll.isAvailable()){
            this.dubboIOWorker = new EpollEventLoopGroup(1, new NamedThreadFactory("gateway-dubbo-io-pool"));
        }else{
            this.dubboIOWorker = new NioEventLoopGroup(1, new NamedThreadFactory("gateway-dubbo-io-pool"));
        }

        codec = new DubboCodec();
        connectionHandler = new DubboConnectionHandler(connectionManager);

        int workCount = SystemPropertyUtil.getInt("gateway.dubbo_client_response_pool_count",
                Math.max(Runtime.getRuntime().availableProcessors() * 2 + 1, 4));
        int queueSize = SystemPropertyUtil.getInt("gateway.dubbo_client_response_queue_size", 100000);
        BlockingQueue<Runnable> blockingQueue= new LinkedBlockingQueue<Runnable>(queueSize);
        executorService=new ThreadPoolExecutor(//
                workCount,//
                workCount,//
                1000 * 60,//
                TimeUnit.MILLISECONDS,//
                blockingQueue,//
                new NamedThreadFactory("gateway-dubbo-response-pool"));
        dubboClientHandler = new DubboClientHandler(executorService);

        if (Epoll.isAvailable()){
            this.bootstrap.channel(EpollSocketChannel.class);
        }else{
            this.bootstrap.channel(NioSocketChannel.class);
        }

        this.bootstrap.group(this.dubboIOWorker)
                //.channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, config(NetworkConfigs.TCP_NODELAY).get())
                .option(ChannelOption.SO_KEEPALIVE, config(NetworkConfigs.SO_KEEPALIVE).get())
                .option(ChannelOption.SO_SNDBUF, config(NetworkConfigs.SO_SNDBUF).get())
                .option(ChannelOption.SO_RCVBUF, config(NetworkConfigs.SO_RCVBUF).get())
                .option(ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK, config(NetworkConfigs.WRITE_BUFFER_HIGH_WATER_MARK).get())
                .option(ChannelOption.WRITE_BUFFER_LOW_WATER_MARK, config(NetworkConfigs.WRITE_BUFFER_LOW_WATER_MARK).get())
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, config(NetworkConfigs.CONNECT_TIMEOUT_MILLIS).get())
                .handler(new ChannelInitializer<SocketChannel>() {

                    public void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(//
                                dubboResponsePool, //TODO 写入线程池和读取线程池职责分离
                                codec.encoder(), //
                                codec.decoder(), //
                                new IdleStateHandler(0, 0, SystemPropertyUtil.getInt("gateway.dubbo_client_max_idle_seconds", 120)),//
                                connectionHandler, //
                                dubboClientHandler
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
