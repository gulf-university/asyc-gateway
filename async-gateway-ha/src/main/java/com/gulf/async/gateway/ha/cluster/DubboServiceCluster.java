package com.gulf.async.gateway.ha.cluster;

import com.async.gateway.register.spi.RegisterFactory;
import com.async.gateway.register.spi.RegisterService;
import com.gulf.async.gateway.spi.Service;
import com.gulf.async.gateway.spi.ha.ServiceCluster;
import com.gulf.async.gateway.spi.ha.ServiceNode;

/**
 * Created by xubai on 2019/10/11 3:17 PM.
 */
public class DubboServiceCluster implements ServiceCluster {



    private RegisterFactory registerFactory;

    public void setRegisterFactory(RegisterFactory registerFactory) {
        this.registerFactory = registerFactory;
    }

    @Override
    public ServiceNode route(Service serviceInfo) {
        RegisterService service = registerFactory.getRegisterService(serviceInfo);
        return service.getRegisterService();
    }
}
