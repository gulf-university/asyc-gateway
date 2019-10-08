package com.gulf.async.gateway.common.log.jdk;

import com.gulf.async.gateway.common.log.Logger;
import com.gulf.async.gateway.common.log.LoggerFactory;

/**
 * Created by xubai on 2019/09/19 7:33 PM.
 */
public class JdkLoggerFactory extends LoggerFactory {

    public final static LoggerFactory INSTANCE = new JdkLoggerFactory();

    @Override
    protected Logger newInstance(String name) {
        return new JdkLogger(java.util.logging.Logger.getLogger(name));
    }
}
