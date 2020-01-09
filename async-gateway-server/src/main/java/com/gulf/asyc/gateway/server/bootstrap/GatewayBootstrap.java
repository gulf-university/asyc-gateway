package com.gulf.asyc.gateway.server.bootstrap;

import com.gulf.async.gateway.common.configuration.ConfigurationManager;
import com.gulf.async.gateway.common.configuration.HttpConfiguration;
import com.gulf.async.gateway.remoting.http.HttpServer;
import com.gulf.async.gateway.remoting.spi.RemotingServer;
import com.gulf.async.gateway.remoting.spi.config.NetworkConfigs;

import java.util.Properties;

/**
 * Created by xubai on 2020/01/09 4:52 PM.
 */
public class GatewayBootstrap extends BaseBootstrap {

    private RemotingServer httpServer;

    protected void doStart(){
        //1.store component init
        //2.http server start
        HttpConfiguration httpConfiguration = (HttpConfiguration)ConfigurationManager.getInstance().getConfiguration(HttpConfiguration.NAME);
        httpServer = new HttpServer(httpConfiguration.getPort());
        httpServer.config(NetworkConfigs.HTTP_REQUEST_TIMEOUT_MILLIS).set(300000L);
        httpServer.start();
        //3.service register start
        //4.metric component start
        //5.
    }

    @Override
    protected void doStop() {
        httpServer.stop();
    }
}
