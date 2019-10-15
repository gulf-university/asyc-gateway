package com.gulf.async.gateway.ha.loadbalance;

import com.gulf.async.gateway.common.log.Logger;
import com.gulf.async.gateway.common.log.LoggerFactory;
import com.gulf.async.gateway.spi.ha.ServiceNode;
import com.gulf.async.gateway.spi.ha.ServiceNodes;

import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by xubai on 2018/11/09 11:39 AM.
 */
public class RandomLoadBalance extends AbstractLoadBalance {

    private final static Logger LOG = LoggerFactory.getInstance(RandomLoadBalance.class);

    private final Random random = new Random();

    @Override
    protected String doSelect(ServiceNodes nodes, Map<String, Object> options) {
        List<ServiceNode> nodeList = nodes.getNodeList();
        int nodeCount = nodeList.size();
        if (nodes.getMaxWeight() != nodes.getMinWeight()){
            int totalWeight = 0;
            boolean same = true;
            for (int i = 0; i < nodeCount; i++) {
                int weight = nodeList.get(i).weight();
                //累积权重
                totalWeight += weight;
                if (i > 0 && weight != nodeList.get(i-1).weight() && same){
                    //标记权重是否一样
                    same = false;
                }
            }
            // 如果权重不相同且权重大于0则按总权重数随机
            if (totalWeight > 0 && !same){
                int randomWeight = random.nextInt(totalWeight);
                for (ServiceNode node : nodeList){
                    randomWeight -= node.weight();
                    if (randomWeight < 0){
                        return node.url();
                    }
                }
            }
        }
        return nodes.getNodeList().get(random.nextInt(nodeCount)).url();
    }

}
