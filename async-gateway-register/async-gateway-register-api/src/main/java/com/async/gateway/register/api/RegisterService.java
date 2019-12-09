package com.async.gateway.register.api;

import com.gulf.async.gateway.spi.ha.ServiceNode;
import com.gulf.async.gateway.spi.ha.ServiceNodes;

import java.util.List;

public interface RegisterService {
    ServiceNode getRegisterService();
}
