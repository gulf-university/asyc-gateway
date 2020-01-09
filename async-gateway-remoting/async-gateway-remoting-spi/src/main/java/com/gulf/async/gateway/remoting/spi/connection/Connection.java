package com.gulf.async.gateway.remoting.spi.connection;

/**
 * Created by xubai on 2019/10/07 5:12 PM.
 */
public interface Connection<T> {

    String id();

    T target();

    boolean alive();

}
