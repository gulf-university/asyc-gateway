package com.async.gateway.register.api;

import com.gulf.async.gateway.spi.ha.ServiceNode;

import java.util.List;

public interface RegisterService {
    List<ServiceNode> getRegisterService();
}
