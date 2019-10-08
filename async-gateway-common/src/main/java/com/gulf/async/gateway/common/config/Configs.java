package com.gulf.async.gateway.common.config;

/**
 * Created by xubai on 2019/09/24 5:02 PM.
 */
public interface Configs {

    <T> ConfigValue<T> config(ConfigOption<T> key);

    <T> boolean contains(ConfigOption<T> key);

}
