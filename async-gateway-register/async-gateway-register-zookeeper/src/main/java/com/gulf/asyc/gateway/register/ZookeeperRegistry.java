package com.gulf.asyc.gateway.register;

import com.async.gateway.register.api.RegisterService;
import com.gulf.async.gateway.spi.ha.ServiceNode;

import java.util.List;

public class ZookeeperRegistry implements RegisterService{

    @Override
    public List<ServiceNode> getRegisterService() {
        return null;
    }
}
