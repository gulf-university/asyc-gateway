package com.gulf.async.gateway.remoting.api.connection;

/**
 * Created by xubai on 2019/10/07 5:12 PM.
 */
public interface Connection<T> {

    String id();

    T target();

}
