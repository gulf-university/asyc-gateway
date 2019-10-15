package com.gulf.async.gateway.pipeline;

import com.gulf.async.gateway.common.spi.SPI;
import com.gulf.async.gateway.remoting.api.context.RemotingContext;

/**
 * Created by xubai on 2019/09/26 11:08 AM.
 */
@SPI
public interface GatewayPipeline<R, T> extends GatewayPlugin {

    R getSource(String identity);

}
