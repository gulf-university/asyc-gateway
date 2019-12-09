package com.async.gateway.register.api;

import com.gulf.async.gateway.common.spi.SPI;

@SPI
public interface RegisterFactory {
    RegisterService getRegisterService(String url);

}
