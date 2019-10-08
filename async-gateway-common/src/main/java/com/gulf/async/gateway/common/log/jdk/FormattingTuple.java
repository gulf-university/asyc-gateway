package com.gulf.async.gateway.common.log.jdk;

/**
 * Holds the results of formatting done by {@link MessageFormatter}.
 */
final class FormattingTuple {

    private final String message;
    private final Throwable throwable;

    FormattingTuple(String message, Throwable throwable) {
        this.message = message;
        this.throwable = throwable;
    }

    public String getMessage() {
        return message;
    }

    public Throwable getThrowable() {
        return throwable;
    }
}