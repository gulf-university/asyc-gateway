package com.gulf.async.gateway.common.configuration;

import java.util.Properties;

/**
 * Created by xubai on 2020/01/09 5:52 PM.
 */
public class HttpConfiguration implements Configuration {

    public final static String NAME = "http_configuration";

    private Integer port;

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public void load(Object source) {
        Properties properties = (Properties)source;
        port = Integer.valueOf(properties.getProperty("http.port", "8899"));
    }

    public Integer getPort() {
        return port;
    }
}
