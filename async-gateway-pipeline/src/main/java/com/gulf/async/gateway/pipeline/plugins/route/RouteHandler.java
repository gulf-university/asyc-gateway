package com.gulf.async.gateway.pipeline.plugins.route;

import com.gulf.async.gateway.common.spi.Activate;
import com.gulf.async.gateway.pipeline.AbstractGatewayPlugin;
import com.gulf.async.gateway.spi.pipeline.GatewayPlugin;
import com.gulf.async.gateway.spi.remote.RemotingContext;

/**
 * Created by xubai on 2019/10/11 12:20 PM.
 */
@Activate(group = {GatewayPlugin.PLUGIN_GROUP}, sort = Integer.MIN_VALUE+1)
public class RouteHandler extends AbstractGatewayPlugin {

    @Override
    public void pre(RemotingContext context) {
        //TODO
    }
}
