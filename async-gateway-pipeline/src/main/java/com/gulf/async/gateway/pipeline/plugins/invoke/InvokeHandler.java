package com.gulf.async.gateway.pipeline.plugins.invoke;

import com.gulf.async.gateway.common.spi.Activate;
import com.gulf.async.gateway.common.spi.SpiLoader;
import com.gulf.async.gateway.pipeline.AbstractGatewayPlugin;
import com.gulf.async.gateway.spi.pipeline.GatewayPlugin;
import com.gulf.async.gateway.spi.remote.RemotingContext;
import com.gulf.async.gateway.remoting.spi.invoke.Invocation;
import com.gulf.async.gateway.remoting.spi.invoke.Invoker;
import com.gulf.async.gateway.remoting.dubbo.DubboInvocation;
import com.gulf.async.gateway.spi.Service;
import com.gulf.async.gateway.spi.ha.ServiceCluster;
import com.gulf.async.gateway.spi.ha.ServiceNode;

/**
 * Created by xubai on 2019/10/11 12:21 PM.
 */
@Activate(group = {GatewayPlugin.PLUGIN_GROUP})
public class InvokeHandler extends AbstractGatewayPlugin {

    private Invoker dubboInvoker;

    private ServiceCluster serviceCluster;

    public InvokeHandler() {
        dubboInvoker = SpiLoader.getSpiLoader(Invoker.class).getAdativateSpi("invoker", "dubbo").get(0);
    }

    @Override
    public void pre(RemotingContext context) {
        //TODO
        Service dubboService = null;
        ServiceNode dubboServiceNode = serviceCluster.route(dubboService);
        Invocation invocation = new DubboInvocation(dubboService, dubboServiceNode, context.request());
        dubboInvoker.invoke(invocation);
    }
}
