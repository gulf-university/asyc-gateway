package com.gulf.async.gateway.ha.loadbalance;

import com.gulf.async.gateway.ha.LoadBalance;
import com.gulf.async.gateway.spi.ha.ServiceNodes;

import java.util.Map;

/**
 * Created by xubai on 2018/11/09 11:43 AM.
 */
public abstract class AbstractLoadBalance implements LoadBalance {

    @Override
    public String select(ServiceNodes nodes, Map<String, Object> options) {
        if (nodes == null
                || nodes.getNodeList() == null || nodes.getNodeList().size() == 0){
            return null;
        }
        if (nodes.getNodeList().size() == 1){
            return nodes.getNodeList().get(0).url();
        }
        return this.doSelect(nodes, options);
    }

    protected abstract String doSelect(ServiceNodes nodes, Map<String, Object> options);

}
