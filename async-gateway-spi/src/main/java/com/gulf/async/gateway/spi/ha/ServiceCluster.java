package com.gulf.async.gateway.spi.ha;

import com.gulf.async.gateway.spi.Service;
import com.gulf.async.gateway.spi.remoting.Endpoint;

/**
 * Created by xubai on 2019/10/11 3:02 PM.
 */
public interface ServiceCluster {

    ServiceNode route(Service serviceInfo);

}
