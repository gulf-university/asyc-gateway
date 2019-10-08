package com.gulf.async.gateway.common.config;

import com.gulf.async.gateway.common.constant.AbstractConstant;
import com.gulf.async.gateway.common.constant.ConstantPool;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by xubai on 2019/09/24 5:03 PM.
 */
public class ConfigOption<T> extends AbstractConstant<ConfigOption<T>> {

    private static final ConstantPool<ConfigOption<Object>> pool = new ConstantPool<ConfigOption<Object>>() {

        @Override
        protected ConfigOption<Object> newConstant(int id, String name) {
            return new ConfigOption<>(id, name);
        }
    };

    /**
     * Returns the {@link ConfigOption} of the specified name.
     */
    public static <T> ConfigOption<T> valueOf(String name) {
        return (ConfigOption<T>) pool.valueOf(name);
    }

    /**
     * Shortcut of {@link #valueOf(String) valueOf(firstNameComponent.getName() + "#" + secondNameComponent)}.
     */
    public static <T> ConfigOption<T> valueOf(Class<?> firstNameComponent, String secondNameComponent) {
        return (ConfigOption<T>) pool.valueOf(firstNameComponent, secondNameComponent);
    }

    /**
     * Creates a new {@link ConfigOption} for the given {@param name} or fail with an
     * {@link IllegalArgumentException} if a {@link ConfigOption} for the given {@param name} exists.
     */
    public static <T> ConfigOption<T> newInstance(String name) {
        return (ConfigOption<T>) pool.newInstance(name);
    }

    /**
     * Returns {@code true} if a {@link ConfigOption} exists for the given {@code name}.
     */
    public static boolean exists(String name) {
        return pool.exists(name);
    }



    private ConfigOption(int id, String name) {
        super(id, name);
    }
    
}
