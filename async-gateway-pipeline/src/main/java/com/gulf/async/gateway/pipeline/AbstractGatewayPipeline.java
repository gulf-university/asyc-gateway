package com.gulf.async.gateway.pipeline;

import com.gulf.async.gateway.common.log.Logger;
import com.gulf.async.gateway.common.log.LoggerFactory;
import com.gulf.async.gateway.common.spi.Activate;
import com.gulf.async.gateway.common.spi.SpiLoader;
import com.gulf.async.gateway.remoting.api.context.RemotingContext;

import java.util.Comparator;
import java.util.List;
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
