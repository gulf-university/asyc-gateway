package com.gulf.async.gateway.remoting.http.connection;

import com.gulf.async.gateway.common.log.Logger;
import com.gulf.async.gateway.common.log.LoggerFactory;
import com.gulf.async.gateway.common.thread.NamedThreadFactory;
import com.gulf.async.gateway.remoting.spi.connection.Connection;
import com.gulf.async.gateway.remoting.spi.connection.ConnectionManager;
import com.gulf.async.gateway.remoting.spi.utils.RemotingUtil;
import com.gulf.async.gateway.remoting.http.HttpResponse;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;

import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by xubai on 2019/10/07 4:55 PM.
 */
public class HttpConnectionManager implements ConnectionManager<Channel> {

    private final static Logger LOG = LoggerFactory.getInstance(HttpConnectionManager.class);

    private final ConcurrentSkipListMap<String, Connection<io.netty.channel.Channel>> connections = new ConcurrentSkipListMap<>();

    private final HashedWheelTimer timer = new HashedWheelTimer(new NamedThreadFactory("gateway-http-connection-timer"));

    private long connectionTimeout;
    private TimeUnit connectionTimeoutUnit;

    public HttpConnectionManager(long timeout, TimeUnit timeoutUnit) {
        this.connectionTimeout = timeout;
        this.connectionTimeoutUnit = timeoutUnit;
    }

    @Override
    public void add(Connection<Channel> connection) {
        Connection<Channel> exist = connections.putIfAbsent(connection.id(), connection);
        if (exist == null){
            timer.newTimeout(new HttpConnectionTimeoutTask(connection),
                    connectionTimeout,
                    connectionTimeoutUnit);
        }
    }

    @Override
    public Connection get(String id) {
        return connections.get(id);
    }

    @Override
    public void remove(String id) {
        Connection<Channel> connection = connections.remove(id);
        if (connection == null){
            return;
        }
        Channel channel = connection.target();
        try {
            if (channel.isActive() && channel.isOpen()){
                channel.close();
            }
        } catch (Exception e) {
            LOG.error("close http connection:{}  exception", RemotingUtil.parseRemoteAddress(channel), e);
        }
        channel = null;
        connection = null;
    }


    class HttpConnectionTimeoutTask implements TimerTask{

        private Connection<Channel> connection;

        public HttpConnectionTimeoutTask(Connection connection) {
            this.connection = connection;
        }

        @Override
        public void run(Timeout timeout) throws Exception {
            //write timeout & close
            Channel channel = connection.target();
            try {
                if (channel.isActive() && channel.isWritable()){
                    FullHttpResponse r = HttpResponse.createFullHttpResponse(new HttpResponse("gateway timeout"), HttpResponseStatus.REQUEST_TIMEOUT);
                    channel.writeAndFlush(r).addListener(ChannelFutureListener.CLOSE);
                }
            } catch (Exception e) {
                LOG.error("http connection:{} process timeout exception", RemotingUtil.parseRemoteAddress(channel), e);
            }finally {
                remove(connection.id());
            }
        }
    }
}
