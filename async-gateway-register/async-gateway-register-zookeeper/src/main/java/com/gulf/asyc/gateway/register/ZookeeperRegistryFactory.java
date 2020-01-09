package com.gulf.asyc.gateway.register;

import com.async.gateway.register.spi.RegisterFactory;
import com.async.gateway.register.spi.RegisterService;
import com.gulf.asyc.gateway.remoting.zookeeper.ZookeeperTransporter;
import com.gulf.async.gateway.spi.Service;

public class ZookeeperRegistryFactory implements RegisterFactory{

    private ZookeeperTransporter zookeeperTransporter;

    public void setZookeeperTransporter(ZookeeperTransporter zookeeperTransporter) {
        this.zookeeperTransporter = zookeeperTransporter;
    }

    @Override
    public RegisterService getRegisterService(Service service) {
        return null;
    }
}
