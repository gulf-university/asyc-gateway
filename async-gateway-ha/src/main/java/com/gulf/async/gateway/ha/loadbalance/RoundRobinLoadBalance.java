package com.gulf.async.gateway.ha.loadbalance;

import com.gulf.async.gateway.common.log.Logger;
import com.gulf.async.gateway.common.log.LoggerFactory;
import com.gulf.async.gateway.spi.ha.ServiceNode;
import com.gulf.async.gateway.spi.ha.ServiceNodes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by xubai on 2018/11/09 11:39 AM.
 */
public class RoundRobinLoadBalance extends AbstractLoadBalance{

    private final static Logger LOG = LoggerFactory.getInstance(RoundRobinLoadBalance.class);

    private final Map<String, AtomicInteger> sequences = new ConcurrentHashMap();
    private final Map<String, AtomicInteger> wgSequences = new ConcurrentHashMap();

    @Override
    protected String doSelect(ServiceNodes nodes, Map<String, Object> options) {
        List<ServiceNode> nodeList = nodes.getNodeList();
        int nodeCount = nodeList.size();
        String serviceName = nodes.getService().name();
        int minWeight = nodes.getMinWeight();
        int maxWeight = nodes.getMaxWeight();
        if(minWeight < maxWeight && maxWeight > 0){
            AtomicInteger wgSequence = wgSequences.get(serviceName);
            if(null == wgSequence){
                wgSequence = new AtomicInteger();
                wgSequences.put(serviceName, wgSequence);
                wgSequence = wgSequences.get(serviceName);
            }
            //轮询取权重基数
            int baseWeight = Math.abs(wgSequence.getAndIncrement() % maxWeight);
            List<ServiceNode> weightNodeList = new ArrayList();
            for(ServiceNode node:nodeList){
                if(node.weight() > baseWeight){
                    weightNodeList.add(node);
                }
            }
            int weightNodeCount = weightNodeList.size();
            if(weightNodeCount == 1){
                return weightNodeList.get(0).url();
            }else{
                nodeCount = weightNodeCount;
                nodeList = weightNodeList;
            }
        }
        AtomicInteger sequence = sequences.get(serviceName);
        if(null == sequence){
            sequence = new AtomicInteger();
            sequences.put(serviceName, sequence);
        }
        //轮询区模
        int index = Math.abs(sequence.getAndIncrement() % nodeCount);
        return nodeList.get(index).url();
    }
}
