package com.gulf.async.gateway.remoting.api;

import com.gulf.async.gateway.common.LifeCycle;
import com.gulf.async.gateway.common.config.Configs;
import com.gulf.async.gateway.spi.remoting.Endpoint;

import java.net.SocketAddress;

/**
 * Created by xubai on 2019/09/19 5:24 PM.
 */
public interface RemotingServer extends Endpoint, LifeCycle, Configs {

    String ip();

    int port();

    SocketAddress localAddress();

}
