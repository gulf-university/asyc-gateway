package com.gulf.async.gateway.spi.pipeline;

import com.gulf.async.gateway.common.spi.SPI;

/**
 * Created by xubai on 2019/09/26 11:08 AM.
 */
@SPI
public interface GatewayPipeline<R, T> extends GatewayPlugin {

    R getSource(String identity);

}
