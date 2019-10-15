package com.gulf.async.gateway.remoting.http;

import com.gulf.async.gateway.common.exception.GatewayException;
import com.gulf.async.gateway.common.log.Logger;
import com.gulf.async.gateway.common.log.LoggerFactory;
import com.gulf.async.gateway.common.thread.NamedThreadFactory;
import com.gulf.async.gateway.common.util.SystemPropertyUtil;
import com.gulf.async.gateway.remoting.api.Codec;
import com.gulf.async.gateway.remoting.api.config.NetworkConfigs;
import com.gulf.async.gateway.remoting.api.connection.ConnectionManager;
import com.gulf.async.gateway.remoting.api.server.AbstractRemotingServer;
import com.gulf.async.gateway.remoting.http.codec.HttpCodec;
import com.gulf.async.gateway.remoting.http.connection.HttpConnectionManager;
import com.gulf.async.gateway.remoting.http.handler.HttpConnectionHandler;
import com.gulf.async.gateway.remoting.http.handler.HttpServerHandler;
import com.gulf.async.gateway.remoting.http.handler.IpFilterHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.epoll.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.util.concurrent.DefaultEventExecutorGroup;

import java.util.concurrent.TimeUnit;

/**
 * Created by xubai on 2019/09/19 5:44 PM.
 */
public class HttpServer extends AbstractRemotingServer {

    private final static Logger LOG = LoggerFactory.getInstance(HttpServer.class);

    //server bootstrap
    private ServerBootstrap serverBootstrap;

    //channelFuture
    private ChannelFuture channelFuture;

    //acceptor
    private EventLoopGroup acceptor;
    //io threadpool
    private EventLoopGroup ioPool;

    private DefaultEventExecutorGroup bussinessPool;

    private IpFilterHandler blackListFilter;

    private ConnectionManager<Channel> connectionManager;
    private HttpConnectionHandler connectionHandler;

    private Codec codec;



    public HttpServer(int port) {
        super(port);
    }

    public HttpServer(String ip, int port) {
        super(ip, port);
    }


    @Override
    protected void initConfigs() {
        config(NetworkConfigs.NETTY_EPOLL_SWITCH).setIfAbsent(NetworkConfigs.NETTY_EPOLL_SWICH_DEFAULT);
        config(NetworkConfigs.SO_BACKLOG).setIfAbsent(NetworkConfigs.TCP_SO_BACKLOG_DEFAULT);
        config(NetworkConfigs.TCP_NODELAY).setIfAbsent(NetworkConfigs.TCP_NODELAY_DEFAULT);
        config(NetworkConfigs.SO_REUSEADDR).setIfAbsent(NetworkConfigs.TCP_SO_REUSEADDR_DEFAULT);
        config(NetworkConfigs.SO_KEEPALIVE).setIfAbsent(NetworkConfigs.TCP_SO_KEEPALIVE_DEFAULT);
        config(NetworkConfigs.NETTY_BUFFER_POOLED).setIfAbsent(NetworkConfigs.NETTY_BUFFER_POOLED_DEFAULT);
        config(NetworkConfigs.NETTY_EPOLL_LT).setIfAbsent(NetworkConfigs.NETTY_EPOLL_LT_DEFAULT);
        config(NetworkConfigs.HTTP_MAX_REQUEST_CONTENT_LENGTH).setIfAbsent(NetworkConfigs.HTTP_MAX_REQUEST_CONTENT_LENGTH_DEFAULT);
        config(NetworkConfigs.HTTP_MAX_RESPONSE_CONTENT_LENGTH).setIfAbsent(NetworkConfigs.HTTP_MAX_RESPONSE_CONTENT_LENGTH_DEFAULT);
        config(NetworkConfigs.HTTP_MAX_INITIAL_LINE_LENGTH).set(NetworkConfigs.HTTP_MAX_INITIAL_LINE_LENGTH_DEFAULT);
        config(NetworkConfigs.HTTP_MAX_HEADER_SIZE).set(NetworkConfigs.HTTP_MAX_HEADER_SIZE_DEFAULT);
        config(NetworkConfigs.HTTP_MAX_CHUNK_SIZE).set(NetworkConfigs.HTTP_MAX_CHUNK_SIZE_DEFAULT);
        config(NetworkConfigs.HTTP_VALIDATE_HEADERS).set(NetworkConfigs.HTTP_VALIDATE_HEADERS_DEFAULT);
        config(NetworkConfigs.HTTP_REQUEST_TIMEOUT_MILLIS).set(NetworkConfigs.HTTP_REQUEST_TIMEOUT_MILLIS_DEFAULT);

        Integer acceptorSize = SystemPropertyUtil.getInt("gateway.acceptor_pool_size", 1);
        Integer workerSize = SystemPropertyUtil.getInt("gateway.worker_pool_size", Runtime.getRuntime().availableProcessors() * 2);
        Integer businessPoolSize = SystemPropertyUtil.getInt("gateway.business_pool_size", Math.max(Runtime.getRuntime().availableProcessors() * 2 +1, 4));

        if (config(NetworkConfigs.NETTY_EPOLL_SWITCH).get() && Epoll.isAvailable()){
            acceptor = new EpollEventLoopGroup(acceptorSize, new NamedThreadFactory("gateway-acceptor"));
            ioPool = new EpollEventLoopGroup(workerSize, new NamedThreadFactory("gateway-io-pool"));
        }else{
            acceptor = new NioEventLoopGroup(1, new NamedThreadFactory("gateway-acceptor"));
            ioPool = new NioEventLoopGroup(workerSize, new NamedThreadFactory("gateway-io-pool"));
        }
        bussinessPool = new DefaultEventExecutorGroup(businessPoolSize, new NamedThreadFactory("gateway-business-pool"));

        this.serverBootstrap = new ServerBootstrap();
        this.serverBootstrap.group(acceptor, ioPool)
                .option(ChannelOption.SO_BACKLOG, config(NetworkConfigs.SO_BACKLOG).get())
                .option(ChannelOption.SO_REUSEADDR, config(NetworkConfigs.SO_REUSEADDR).get())
                .childOption(ChannelOption.TCP_NODELAY, config(NetworkConfigs.TCP_NODELAY).get())
                .childOption(ChannelOption.SO_KEEPALIVE, config(NetworkConfigs.SO_KEEPALIVE).get());
        //set ByteBuf Allocator
        if (config(NetworkConfigs.NETTY_BUFFER_POOLED).get()) {
            this.serverBootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        } else {
            this.serverBootstrap.option(ChannelOption.ALLOCATOR, UnpooledByteBufAllocator.DEFAULT)
                    .childOption(ChannelOption.ALLOCATOR, UnpooledByteBufAllocator.DEFAULT);
        }
        //set LT | ET
        if (config(NetworkConfigs.NETTY_EPOLL_SWITCH).get() && Epoll.isAvailable()){
            if (config(NetworkConfigs.NETTY_EPOLL_LT).get()) {
                serverBootstrap.childOption(EpollChannelOption.EPOLL_MODE, EpollMode.LEVEL_TRIGGERED);
            } else {
                serverBootstrap.childOption(EpollChannelOption.EPOLL_MODE, EpollMode.EDGE_TRIGGERED);
            }
        }
        //set channel
        if (config(NetworkConfigs.NETTY_EPOLL_SWITCH).get() && Epoll.isAvailable()){
            serverBootstrap.channel(EpollServerSocketChannel.class);
        }else{
            serverBootstrap.channel(NioServerSocketChannel.class);
        }
        //set blackList filter
        this.blackListFilter = new IpFilterHandler();
        this.blackListFilter.loadAllIp();
        this.connectionManager = new HttpConnectionManager(config(NetworkConfigs.HTTP_REQUEST_TIMEOUT_MILLIS).get(), TimeUnit.MILLISECONDS);
        this.connectionHandler = new HttpConnectionHandler(connectionManager);
        //set codec
        this.codec = new HttpCodec(config(NetworkConfigs.HTTP_MAX_INITIAL_LINE_LENGTH).get(),
                config(NetworkConfigs.HTTP_MAX_HEADER_SIZE).get(),
                config(NetworkConfigs.HTTP_MAX_CHUNK_SIZE).get(),
                config(NetworkConfigs.HTTP_VALIDATE_HEADERS).get());
        //set initializer
        serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast("ip_filter", blackListFilter);
                //move to connection manager
                //ch.pipeline().addLast("timeout_handler", new TimeoutHandler());
                ch.pipeline().addLast("http_decoder", codec.decoder());
                ch.pipeline().addLast("request_aggregator", new HttpObjectAggregator(config(NetworkConfigs.HTTP_MAX_REQUEST_CONTENT_LENGTH).get()));
                ch.pipeline().addLast("http_encoder", codec.encoder());
                ch.pipeline().addLast("response_aggregator", new HttpObjectAggregator(config(NetworkConfigs.HTTP_MAX_RESPONSE_CONTENT_LENGTH).get()));
                ch.pipeline().addLast(bussinessPool,
                        connectionHandler, new HttpServerHandler());
            }
        });
    }

    @Override
    protected void startServer() {
        try {
            this.channelFuture = serverBootstrap.bind(ip, port).sync();
        } catch (InterruptedException e) {
            throw new GatewayException("start http server[ip:"+ip+" port:"+port+"] exception");
        }finally {
            super.stop();
        }
    }

    @Override
    protected void shutdownServer() {
        if(channelFuture != null){
            channelFuture.channel().close();
        }
        if (acceptor != null){
            acceptor.shutdownGracefully();
        }
        if (ioPool != null){
            ioPool.shutdownGracefully();
        }
        if (bussinessPool != null){
            bussinessPool.shutdownGracefully();
        }
    }

    public IpFilterHandler getBlackListFilter() {
        return blackListFilter;
    }

}
