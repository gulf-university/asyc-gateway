package com.gulf.async.gateway.remoting.spi;

import com.gulf.async.gateway.common.LifeCycle;
import com.gulf.async.gateway.spi.remoting.Endpoint;

/**
 * Created by xubai on 2019/09/19 5:24 PM.
 */
public interface RemotingClient<T> extends Endpoint, LifeCycle {

    T connect(String url);

}
