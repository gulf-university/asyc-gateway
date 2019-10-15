package com.gulf.async.gateway.common.serialize.FST;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by xubai on 2018/11/07 5:30 PM.
  */
public class SerializableClassRegistry {

    private static final Set<Class> registrations = new LinkedHashSet<Class>();

    public static void registerClass(Class clazz) {
        registrations.add(clazz);
    }

    public static Set<Class> getRegisteredClasses() {
        return registrations;
    }
}
