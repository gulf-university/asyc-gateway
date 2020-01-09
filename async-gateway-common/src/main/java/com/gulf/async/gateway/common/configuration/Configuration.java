package com.gulf.async.gateway.common.configuration;

/**
 * Created by xubai on 2020/01/09 5:42 PM.
 */
public interface Configuration {

    String name();

    void load(Object source);

}
