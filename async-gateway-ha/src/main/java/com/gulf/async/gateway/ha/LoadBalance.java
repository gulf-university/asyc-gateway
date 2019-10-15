package com.gulf.async.gateway.ha;

import com.gulf.async.gateway.common.spi.SPI;
import com.gulf.async.gateway.spi.ha.ServiceNodes;

import java.util.Map;

/**
 * Created by xubai on 2018/11/09 11:38 AM.
 */
@SPI
public interface LoadBalance {

    String select(ServiceNodes nodes, Map<String, Object> options);

}
