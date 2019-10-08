package com.gulf.async.gateway.common.exception;

/**
 * Created by xubai on 2019/09/19 7:53 PM.
 */
public class GatewayException extends RuntimeException {

    public GatewayException() {
    }

    public GatewayException(String message) {
        super(message);
    }

    public GatewayException(String message, Throwable cause) {
        super(message, cause);
    }

    public GatewayException(Throwable cause) {
        super(cause);
    }
}
