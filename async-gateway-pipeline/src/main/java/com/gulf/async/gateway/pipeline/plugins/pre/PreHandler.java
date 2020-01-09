package com.gulf.async.gateway.pipeline.plugins.pre;

import com.gulf.async.gateway.common.log.Logger;
import com.gulf.async.gateway.common.log.LoggerFactory;
import com.gulf.async.gateway.common.spi.Activate;
import com.gulf.async.gateway.pipeline.AbstractGatewayPlugin;
import com.gulf.async.gateway.spi.pipeline.GatewayPlugin;
import com.gulf.async.gateway.spi.remote.RemotingContext;

/**
 * Created by xubai on 2019/10/11 11:52 AM.
 */
@Activate(group = {GatewayPlugin.PLUGIN_GROUP}, sort = Integer.MIN_VALUE)
public class PreHandler extends AbstractGatewayPlugin {

    private final static Logger LOG = LoggerFactory.getInstance(PreHandler.class);

    @Override
    public void pre(RemotingContext context) {
        LOG.debug("pre handler, request:{}", context.request());
        //TODO
        super.pre(context);
    }
}
