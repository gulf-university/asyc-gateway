package com.gulf.asyc.gateway.remoting.zookeeper;

import com.gulf.async.gateway.common.spi.SPI;

@SPI("curator")
public interface ZookeeperTransporter {
    ZookeeperClient connect(String url);
}
