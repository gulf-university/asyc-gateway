package com.gulf.async.gateway.common.log.log4j;

import com.gulf.async.gateway.common.log.AbstractLogger;
import com.gulf.async.gateway.common.log.Level;
import com.gulf.async.gateway.common.log.Logger;
import org.apache.logging.log4j.spi.ExtendedLogger;
import org.apache.logging.log4j.spi.ExtendedLoggerWrapper;

/**
 * Created by xubai on 2019/09/19 8:27 PM.
 */
public class Log4jLogger extends ExtendedLoggerWrapper implements Logger {

    /** {@linkplain AbstractInternalLogger#EXCEPTION_MESSAGE} */
    private static final String EXCEPTION_MESSAGE = "Unexpected exception:";

    Log4jLogger(org.apache.logging.log4j.Logger logger) {
        super((ExtendedLogger) logger, logger.getName(), logger.getMessageFactory());
    }

    @Override
    public String name() {
        return getName();
    }

    @Override
    public void trace(Throwable t) {
        log(Level.TRACE, EXCEPTION_MESSAGE, t);
    }

    @Override
    public void debug(Throwable t) {
        log(Level.DEBUG, EXCEPTION_MESSAGE, t);
    }

    @Override
    public void info(Throwable t) {
        log(Level.INFO, EXCEPTION_MESSAGE, t);
    }

    @Override
    public void warn(Throwable t) {
        log(Level.WARN, EXCEPTION_MESSAGE, t);
    }

    @Override
    public void error(Throwable t) {
        log(Level.ERROR, EXCEPTION_MESSAGE, t);
    }

    @Override
    public boolean isEnabled(Level level) {
        return isEnabled(toLevel(level));
    }

    @Override
    public void log(Level level, String msg) {
        log(toLevel(level), msg);
    }

    @Override
    public void log(Level level, String format, Object arg) {
        log(toLevel(level), format, arg);
    }

    @Override
    public void log(Level level, String format, Object argA, Object argB) {
        log(toLevel(level), format, argA, argB);
    }

    @Override
    public void log(Level level, String format, Object... arguments) {
        log(toLevel(level), format, arguments);
    }

    @Override
    public void log(Level level, String msg, Throwable t) {
        log(toLevel(level), msg, t);
    }

    @Override
    public void log(Level level, Throwable t) {
        log(toLevel(level), EXCEPTION_MESSAGE, t);
    }

    protected Level toLevel(Level level) {
        switch (level) {
            case INFO:
                return Level.INFO;
            case DEBUG:
                return Level.DEBUG;
            case WARN:
                return Level.WARN;
            case ERROR:
                return Level.ERROR;
            case TRACE:
                return Level.TRACE;
            default:
                throw new Error();
        }
    }

}
