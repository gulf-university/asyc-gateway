package com.gulf.asyc.gateway.server.bootstrap;

import com.gulf.async.gateway.common.LifeCycle;
import com.gulf.async.gateway.common.log.Logger;
import com.gulf.async.gateway.common.log.LoggerFactory;
import com.gulf.async.gateway.remoting.http.HttpServer;
import com.gulf.async.gateway.remoting.spi.RemotingServer;

import java.io.Closeable;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by xubai on 2020/01/09 4:23 PM.
 */
public abstract class BaseBootstrap implements LifeCycle, Closeable {

    protected final static Logger LOG = LoggerFactory.getInstance("bootstrap_log");

    protected final AtomicBoolean RUNNING = new AtomicBoolean(false);


    @Override
    public void start() {
        if (!RUNNING.compareAndSet(false, true)){
            LOG.warn("the bootstrap has started!");
            return;
        }
        try {
            doStart();
        } catch (Exception e) {
            throw new RuntimeException("bootstarp start exception", e);
        }
    }

    protected abstract void doStart();

    @Override
    public void stop() {
        if (!RUNNING.compareAndSet(true, false)){
            LOG.warn("the bootstrap has stoped!");
            return;
        }
        //TODO
        try {
            doStop();
        } catch (Exception e) {
            throw new RuntimeException("bootstarp stop exception", e);
        }
    }

    protected abstract void doStop();

    @Override
    public boolean isRunning() {
        return RUNNING.get();
    }

    @Override
    public void close() throws IOException {
        stop();
    }

}
