package com.gulf.async.gateway.remoting.api.invoke;

import com.gulf.async.gateway.remoting.api.Protocol;
import com.gulf.async.gateway.remoting.api.context.RemotingContext;
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
