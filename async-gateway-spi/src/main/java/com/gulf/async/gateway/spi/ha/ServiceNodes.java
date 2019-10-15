package com.gulf.async.gateway.spi.ha;

import com.google.common.collect.ImmutableList;
import com.gulf.async.gateway.spi.Service;
import lombok.Data;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by xubai on 2019/10/11 2:49 PM.
 */
@Data
public abstract class ServiceNodes {

    protected Service service;

    protected Map<String, ServiceNode> nodes = new ConcurrentHashMap<>();

    protected int maxWeight = -1;//最大权重

    protected int minWeight = -1;//最小权重


    public List<ServiceNode> getNodeList() {
        return ImmutableList.copyOf(nodes.values());
    }

    public ServiceNode getRandomNode() {
        List<ServiceNode> nodeList = getNodeList();
        //TODO optimize
        Random random = new Random();
        return nodeList.get(random.nextInt(nodeList.size()));
    }

}
