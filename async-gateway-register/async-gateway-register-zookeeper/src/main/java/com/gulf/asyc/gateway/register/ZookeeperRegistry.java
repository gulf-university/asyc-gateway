package com.gulf.asyc.gateway.register;

import com.async.gateway.register.api.RegisterService;
import com.gulf.async.gateway.spi.ha.ServiceNode;
import com.gulf.async.gateway.spi.ha.ServiceNodes;

public class ZookeeperRegistry extends ServiceNodes implements RegisterService{

    @Override
    public ServiceNode getRegisterService() {
        return getRandomNode();
    }
}
