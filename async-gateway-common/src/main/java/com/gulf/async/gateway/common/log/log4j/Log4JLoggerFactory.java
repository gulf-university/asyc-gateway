package com.gulf.async.gateway.common.log.log4j;

import com.gulf.async.gateway.common.log.Logger;
import com.gulf.async.gateway.common.log.LoggerFactory;
import org.apache.logging.log4j.LogManager;

/**
 * Created by xubai on 2019/09/19 7:33 PM.
 */
public class Log4JLoggerFactory extends LoggerFactory {

    public final static LoggerFactory INSTANCE = new Log4JLoggerFactory();


    @Override
    protected Logger newInstance(String name) {
        return new Log4jLogger(LogManager.getLogger(name));
    }
}
