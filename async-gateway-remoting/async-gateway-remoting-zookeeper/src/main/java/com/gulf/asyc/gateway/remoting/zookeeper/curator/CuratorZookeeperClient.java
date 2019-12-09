package com.gulf.asyc.gateway.remoting.zookeeper.curator;

import com.gulf.asyc.gateway.remoting.zookeeper.ZookeeperClient;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.KeeperException;

import java.util.List;

public class CuratorZookeeperClient implements ZookeeperClient{

    private final CuratorFramework client;

    public CuratorZookeeperClient(String url) {
        CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory.builder()
                .connectString(url)
                .retryPolicy(new RetryNTimes(Integer.MAX_VALUE, 1000))
                .connectionTimeoutMs(5000)
                .sessionTimeoutMs(10000);

        client = builder.build();

        client.start();
    }

    @Override
    public List<String> getChildren(String path) {
        try {
            return client.getChildren().forPath(path);
        } catch (KeeperException.NoNodeException e) {
            return null;
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }
}
