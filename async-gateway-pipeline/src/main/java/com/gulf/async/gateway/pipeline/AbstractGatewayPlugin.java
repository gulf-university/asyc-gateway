package com.gulf.async.gateway.pipeline;

import com.gulf.async.gateway.spi.remote.RemotingContext;
import com.gulf.async.gateway.spi.pipeline.GatewayPlugin;

/**
 * Created by xubai on 2019/10/11 11:52 AM.
 */
public abstract class AbstractGatewayPlugin implements GatewayPlugin {

    @Override
    public void pre(RemotingContext context) {
        //default noop, subclass implement
    }

    @Override
    public void post(RemotingContext context) {
        //default noop, subclass implement
    }
}
