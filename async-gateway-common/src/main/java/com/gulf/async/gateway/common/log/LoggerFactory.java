package com.gulf.async.gateway.common.log;

import com.gulf.async.gateway.common.log.jdk.JdkLoggerFactory;
import com.gulf.async.gateway.common.log.log4j.Log4JLoggerFactory;
import com.gulf.async.gateway.common.log.slf4j.Slf4jLoggerFactory;

/**
 * Created by xubai on 2019/09/19 7:27 PM.
 */
public abstract class LoggerFactory {

    private static volatile LoggerFactory defaultFactory;

    public static LoggerFactory newDefaultFactory(String name){
        LoggerFactory f;
        try {
            f = new Slf4jLoggerFactory(true);
            f.newInstance(name).debug("Using SLF4J as the default logging framework");
        } catch (Throwable t1) {
            try {
                f = Log4JLoggerFactory.INSTANCE;
                f.newInstance(name).debug("Using Log4J as the default logging framework");
            } catch (Throwable t2) {
                f = JdkLoggerFactory.INSTANCE;
                f.newInstance(name).debug("Using java.util.logging as the default logging framework");
            }
        }
        return f;
    }

    /**
     * Returns the default factory.  The initial default factory is
     * {@link JdkLoggerFactory}.
     */
    public static LoggerFactory getDefaultFactory() {
        if (defaultFactory == null) {
            defaultFactory = newDefaultFactory(LoggerFactory.class.getName());
        }
        return defaultFactory;
    }

    /**
     * Changes the default factory.
     */
    public static void setDefaultFactory(LoggerFactory defaultFactory) {
        if (defaultFactory == null) {
            throw new NullPointerException("defaultFactory");
        }
        LoggerFactory.defaultFactory = defaultFactory;
    }

    /**
     * Creates a new logger instance with the name of the specified class.
     */
    public static Logger getInstance(Class<?> clazz) {
        return getInstance(clazz.getName());
    }

    /**
     * Creates a new logger instance with the specified name.
     */
    public static Logger getInstance(String name) {
        return getDefaultFactory().newInstance(name);
    }

    /**
     * Creates a new logger instance with the specified name.
     */
    protected abstract Logger newInstance(String name);

}
