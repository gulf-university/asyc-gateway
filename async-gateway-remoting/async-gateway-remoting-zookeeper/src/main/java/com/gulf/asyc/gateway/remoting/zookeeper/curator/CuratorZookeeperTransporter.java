package com.gulf.asyc.gateway.remoting.zookeeper.curator;

import com.gulf.asyc.gateway.remoting.zookeeper.ZookeeperClient;
import com.gulf.asyc.gateway.remoting.zookeeper.ZookeeperTransporter;

public class CuratorZookeeperTransporter implements ZookeeperTransporter {

    @Override
    public ZookeeperClient connect(String url) {
        return new CuratorZookeeperClient(url);
    }
}
