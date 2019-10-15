package com.gulf.async.gateway.pipeline.plugins.error;

import com.gulf.async.gateway.common.spi.Activate;
import com.gulf.async.gateway.pipeline.AbstractGatewayPlugin;
import com.gulf.async.gateway.pipeline.GatewayPlugin;
import com.gulf.async.gateway.remoting.api.context.RemotingContext;

/**
 * Created by xubai on 2019/10/11 12:22 PM.
 */
@Activate(group = {GatewayPlugin.PLUGIN_GROUP}, sort = Integer.MIN_VALUE)
public class ExceptionHandler extends AbstractGatewayPlugin {

    @Override
    public void post(RemotingContext context) {
        //TODO
        super.post(context);
    }
}
