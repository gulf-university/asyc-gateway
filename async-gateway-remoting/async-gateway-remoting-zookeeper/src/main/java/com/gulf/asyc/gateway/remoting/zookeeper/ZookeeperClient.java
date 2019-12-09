package com.gulf.asyc.gateway.remoting.zookeeper;

import java.util.List;

public interface ZookeeperClient {
    List<String> getChildren(String path);
}
