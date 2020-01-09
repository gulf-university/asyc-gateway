package com.gulf.async.gateway.remoting.spi.invoke;

import com.gulf.async.gateway.spi.remote.Protocol;
import com.gulf.async.gateway.spi.Service;
import com.gulf.async.gateway.spi.ha.ServiceNode;

/**
 * Created by xubai on 2019/10/15 7:55 PM.
 */
public interface Invocation {

    Service service();

    ServiceNode serviceNode();

    Protocol command();

}
