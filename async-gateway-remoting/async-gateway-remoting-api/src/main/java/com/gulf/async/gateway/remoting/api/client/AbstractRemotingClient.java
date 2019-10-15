package com.gulf.async.gateway.remoting.api.client;

import com.gulf.async.gateway.common.config.DefaultConfigs;
import com.gulf.async.gateway.common.log.Logger;
import com.gulf.async.gateway.common.log.LoggerFactory;
import com.gulf.async.gateway.remoting.api.RemotingClient;
import com.gulf.async.gateway.remoting.api.context.RemotingContext;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by xubai on 2019/09/19 5:42 PM.
 */
public abstract class AbstractRemotingClient<T> extends DefaultConfigs implements RemotingClient<T> {

    private static final Logger LOG = LoggerFactory.getInstance(AbstractRemotingClient.class);

    protected AtomicBoolean living = new AtomicBoolean(false);


    @Override
    public void start() {
        if (!living.compareAndSet(false, true)){
            return;
        }
        try{
            //init configs
            initConfigs();
        }catch (Exception e){
            this.stop();
        }
    }

    @Override
    public void stop() {
        if (!living.compareAndSet(true, false)){
            return;
        }
        try{
            disconnect();
        }catch (Exception e){
            throw new IllegalStateException("disconnect remoting client exception", e);
        }
    }

    @Override
    public boolean isRunning() {
        return living.get();
    }

    protected abstract void initConfigs();

    protected abstract void disconnect();




}
