package com.gulf.async.gateway.ha.cluster;

import com.gulf.async.gateway.spi.Service;
import com.gulf.async.gateway.spi.ha.ServiceCluster;
import com.gulf.async.gateway.spi.remoting.Endpoint;

/**
 * Created by xubai on 2019/10/11 3:17 PM.
 */
public class DubboServiceCluster implements ServiceCluster {

    @Override
    public Endpoint route(Service serviceInfo) {
        return null;
    }
}
