package com.async.gateway.register.spi;

import com.gulf.async.gateway.common.spi.SPI;
import com.gulf.async.gateway.spi.Service;

@SPI
public interface RegisterFactory {
    RegisterService getRegisterService(Service service);
}
