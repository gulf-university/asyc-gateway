package com.gulf.asyc.gateway.server;

import com.gulf.asyc.gateway.server.bootstrap.BaseBootstrap;
import com.gulf.asyc.gateway.server.bootstrap.GatewayBootstrap;
import com.gulf.async.gateway.remoting.http.HttpServer;
import com.gulf.async.gateway.remoting.spi.RemotingServer;

import java.util.Properties;

/**
 * Hello world!
 *
 */
public class GatewayServer
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        BaseBootstrap bootstrap = new GatewayBootstrap();
        bootstrap.start();
    }
}
