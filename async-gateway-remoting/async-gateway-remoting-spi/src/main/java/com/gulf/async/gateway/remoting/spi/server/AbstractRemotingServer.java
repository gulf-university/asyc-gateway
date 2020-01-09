package com.gulf.async.gateway.remoting.spi.server;

import com.gulf.async.gateway.common.config.DefaultConfigs;
import com.gulf.async.gateway.remoting.spi.RemotingServer;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by xubai on 2019/09/19 5:42 PM.
 */
public abstract class AbstractRemotingServer extends DefaultConfigs implements RemotingServer {

    protected String ip;
    protected int port;

    protected SocketAddress localAddress;

    private AtomicBoolean isRunning = new AtomicBoolean(false);

    public AbstractRemotingServer(int port) {
        this.port = port;
        this.localAddress = new InetSocketAddress(port);
        this.ip = ((InetSocketAddress)localAddress).getHostName();
    }

    public AbstractRemotingServer(String ip, int port) {
        this.ip = ip;
        this.port = port;
        this.localAddress = new InetSocketAddress(ip, port);
    }

    @Override
    public String ip() {
        return ip;
    }

    @Override
    public int port() {
        return port;
    }

    @Override
    public SocketAddress localAddress() {
        return localAddress;
    }

    @Override
    public void start() {
        if (!isRunning.compareAndSet(false, true)){
            throw new IllegalStateException("start server:"+localAddress+" is running.");
        }
        //init configs
        initConfigs();
        try{
            startServer();
        }catch (Exception e){
            this.stop();
        }
    }

    @Override
    public void stop() {
        if (!isRunning.compareAndSet(true, false)){
            return;
        }
        try{
            shutdownServer();
        }catch (Exception e){
            throw new IllegalStateException("shutdown server:"+localAddress+" exception", e);
        }
    }

    @Override
    public boolean isRunning() {
        return isRunning.get();
    }

    protected abstract void initConfigs();

    protected abstract void startServer();

    protected abstract void shutdownServer();

}
