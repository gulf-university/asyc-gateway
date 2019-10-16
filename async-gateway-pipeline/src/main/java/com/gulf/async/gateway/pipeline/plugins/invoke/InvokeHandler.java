package com.gulf.async.gateway.pipeline.plugins.invoke;

import com.gulf.async.gateway.common.spi.SpiLoader;
import com.gulf.async.gateway.pipeline.AbstractGatewayPlugin;
import com.gulf.async.gateway.remoting.api.context.RemotingContext;
import com.gulf.async.gateway.remoting.api.invoke.Invocation;
import com.gulf.async.gateway.remoting.api.invoke.Invoker;

/**
 * Created by xubai on 2019/10/11 12:21 PM.
 */
public class InvokeHandler extends AbstractGatewayPlugin {

    private Invoker dubboInvoker;

    public InvokeHandler() {
        dubboInvoker = SpiLoader.getSpiLoader(Invoker.class).getAdativateSpi("invoker", "dubbo").get(0);
    }

    @Override
    public void pre(RemotingContext context) {
        //TODO

    }
}
