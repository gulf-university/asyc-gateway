package com.gulf.async.gateway.common.log.slf4j;

import com.gulf.async.gateway.common.log.Logger;
import com.gulf.async.gateway.common.log.LoggerFactory;
import org.slf4j.helpers.NOPLoggerFactory;

/**
 * Created by xubai on 2019/09/19 7:30 PM.
 */
public class Slf4jLoggerFactory extends LoggerFactory {

    public Slf4jLoggerFactory(boolean failIfNOP) {
        assert failIfNOP; // Should be always called with true.
        if (org.slf4j.LoggerFactory.getILoggerFactory() instanceof NOPLoggerFactory) {
            throw new NoClassDefFoundError("NOPLoggerFactory not supported");
        }
    }

    @Override
    public Logger newInstance(String name) {
        return new Slf4jLogger(org.slf4j.LoggerFactory.getLogger(name));
    }

}
