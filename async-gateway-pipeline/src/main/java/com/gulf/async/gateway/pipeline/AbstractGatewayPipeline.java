package com.gulf.async.gateway.pipeline;

import com.gulf.async.gateway.spi.remote.RemotingContext;
import com.gulf.async.gateway.spi.pipeline.GatewayPipeline;

import java.util.concurrent.ConcurrentSkipListMap;

/**
 * Created by xubai on 2019/10/10 8:05 PM.
 */
public abstract class AbstractGatewayPipeline implements GatewayPipeline<RemotingContext, RemotingContext> {

    protected final ConcurrentSkipListMap<String, RemotingContext> contextCache = new ConcurrentSkipListMap();


    @Override
    public void pre(RemotingContext context) {
        GatewayPluginChain.preInvoke(context);
        contextCache.put(context.request().id(), context);
    }

    @Override
    public void post(RemotingContext context) {
        GatewayPluginChain.postInvoke(context);
        contextCache.remove(context.request().id());
    }

    @Override
    public RemotingContext getSource(String identity) {
        return contextCache.get(identity);
    }
}
