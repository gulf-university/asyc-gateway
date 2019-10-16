package com.gulf.async.gateway.common.exception;

/**
 * Created by xubai on 2019/10/16 10:46 AM.
 */
public class GatewayServiceNotFoundException extends RuntimeException {

    public GatewayServiceNotFoundException() {
    }

    public GatewayServiceNotFoundException(String message) {
        super(message);
    }

    public GatewayServiceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public GatewayServiceNotFoundException(Throwable cause) {
        super(cause);
    }
}
