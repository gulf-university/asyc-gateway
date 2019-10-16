package com.gulf.async.gateway.remoting.dubbo;

import com.gulf.async.gateway.common.exception.GatewayException;
import com.gulf.async.gateway.common.exception.GatewayServiceNotFoundException;
import com.gulf.async.gateway.common.log.Logger;
import com.gulf.async.gateway.common.log.LoggerFactory;
import com.gulf.async.gateway.common.util.SystemPropertyUtil;
import com.gulf.async.gateway.remoting.api.connection.Connection;
import com.gulf.async.gateway.remoting.api.connection.ConnectionManager;
import com.gulf.async.gateway.remoting.api.invoke.Invocation;
import com.gulf.async.gateway.remoting.api.invoke.Invoker;
import com.gulf.async.gateway.remoting.api.utils.RemotingUtil;
import com.gulf.async.gateway.remoting.dubbo.connection.DubboConnection;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by xubai on 2019/10/15 3:32 PM.
 */
public class DubboClients implements ConnectionManager<ChannelFuture>, Invoker {

    private final static Logger LOG = LoggerFactory.getInstance(DubboClients.class);

    private static final long LockTimeoutMillis = 3000;

    private DubboNettyClient dubboNettyClient;

    private ConcurrentHashMap<String/*addr*/, DubboConnection> channelTables = new ConcurrentHashMap<String, DubboConnection>();

    private ReentrantLock channelTablesLock = new ReentrantLock(false);

    @Override
    public void add(Connection<ChannelFuture> connection) {
        channelTables.put(connection.id(), (DubboConnection)connection);
    }

    @Override
    public Connection get(String id) {
        DubboConnection exist = channelTables.get(id);
        if (exist != null){
            return exist;
        }
        try {
            getOrCreateChannel(id);
        } catch (Exception e) {
            throw new GatewayException("get remote adress:"+id+" connection exception", e);
        }
        return channelTables.get(id);
    }

    @Override
    public void remove(String id) {
        closeChannel(id);
    }

    @Override
    public void invoke(Invocation invocation) {
        String url = invocation.serviceNode().url();
        DubboConnection connection = null;
        try {
            connection = (DubboConnection)get(url);
        } catch (Exception e) {
            throw new GatewayServiceNotFoundException("service:"+invocation.service()+" node:"+invocation.serviceNode().url()+" not found");
        }
        Channel channel = connection.target().channel();
        channel.writeAndFlush(invocation.command());
    }

    private Connection<ChannelFuture> getOrCreateChannel(final String addr) throws InterruptedException {
        DubboConnection cw = this.channelTables.get(addr);
        if (cw != null && cw.alive()) {
            return cw;
        }

        // 进入临界区后，不能有阻塞操作，网络连接采用异步方式
        if (this.channelTablesLock.tryLock(LockTimeoutMillis, TimeUnit.MILLISECONDS)) {
            try {
                boolean createNewConnection = false;
                cw = this.channelTables.get(addr);
                if (cw != null) {
                    if (cw.alive()) {// channel正常
                        return cw;
                    } else if (!cw.target().isDone()) { // 正在连接，退出锁等待
                        createNewConnection = false;
                    } else {// 说明连接不成功
                        this.channelTables.remove(addr);
                        createNewConnection = true;
                    }
                } else { // ChannelWrapper不存在
                    createNewConnection = true;
                }

                if (createNewConnection) {
                    ChannelFuture channelFuture = dubboNettyClient.connect(addr);
                    //log.info("createChannel: begin to connect remote host[{}] asynchronously", addr);
                    cw = new DubboConnection(addr, channelFuture);
                    this.channelTables.put(addr, cw);
                }
            } catch (Exception e) {
                LOG.error("createChannel: create channel exception", e);
            } finally {
                this.channelTablesLock.unlock();
            }
        } else {
            LOG.warn("createChannel: try to lock channel table, but timeout, {}ms", LockTimeoutMillis);
        }

        if (cw != null) {
            ChannelFuture channelFuture = cw.target();
            long clientConnectTimeoutMillis = SystemPropertyUtil.getLong("gateway.dubbo_client_connect_timeout_millis", 30000);
            if (channelFuture.awaitUninterruptibly(clientConnectTimeoutMillis)) {
                if (cw.alive()) {
                    //log.info("createChannel: connect remote host[{}] success, {}", addr, channelFuture.toString());
                    return cw;
                } else {
                    LOG.warn("createChannel: connect remote host[" + addr + "] failed, " + channelFuture.toString(), channelFuture.cause());
                }
            } else {
                LOG.warn("createChannel: connect remote host[{}] timeout {}ms, {}", addr, clientConnectTimeoutMillis, channelFuture.toString());
            }
        }
        return null;
    }

    public void closeChannel(final String addr) {
        DubboConnection prevCW = this.channelTables.get(addr);
        if (prevCW == null){
            return;
        }
        Channel channel = prevCW.target().channel();
        final String addrRemote = RemotingUtil.parseRemoteAddress(channel);
        LOG.info("closeChannel: begin close the channel[{}] Found: {}", addrRemote, (prevCW != null));
        try {
            if (this.channelTablesLock.tryLock(LockTimeoutMillis, TimeUnit.MILLISECONDS)) {
                try {
                    this.channelTables.remove(addrRemote);
                    RemotingUtil.closeChannel(channel);
                    LOG.info("closeChannel: the channel[{}] was removed from channel table", addrRemote);
                } catch (Exception e) {
                    LOG.error("closeChannel: close the channel exception", e);
                } finally {
                    this.channelTablesLock.unlock();
                }
            } else {
                LOG.warn("closeChannel: try to lock channel table, but timeout, {}ms", LockTimeoutMillis);
            }
        } catch (InterruptedException e) {
            LOG.error("closeChannel exception", e);
        }
    }
}
