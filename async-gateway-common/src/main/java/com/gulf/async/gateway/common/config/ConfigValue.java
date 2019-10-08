package com.gulf.async.gateway.common.config;

/**
 * Created by xubai on 2019/09/24 8:54 PM.
 */
public interface ConfigValue<T> {

    ConfigOption<T> key();

    T get();

    void set(T value);

    T getAndSet(T value);

    T setIfAbsent(T value);

    boolean compareAndSet(T oldValue, T newValue);

    T getAndRemove();

}
