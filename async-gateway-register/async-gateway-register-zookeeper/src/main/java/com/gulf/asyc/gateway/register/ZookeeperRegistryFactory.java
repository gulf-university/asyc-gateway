package com.gulf.asyc.gateway.register;

import com.async.gateway.register.api.RegisterFactory;
import com.async.gateway.register.api.RegisterService;
import com.gulf.asyc.gateway.remoting.zookeeper.ZookeeperTransporter;

public class ZookeeperRegistryFactory implements RegisterFactory{

    private ZookeeperTransporter zookeeperTransporter;

    public void setZookeeperTransporter(ZookeeperTransporter zookeeperTransporter) {
        this.zookeeperTransporter = zookeeperTransporter;
    }

    @Override
    public RegisterService getRegisterService(String url) {
        return new ZookeeperRegistry();
    }
}
