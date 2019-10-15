package com.gulf.async.gateway.remoting.dubbo.utils;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by xubai on 2018/11/07 5:30 PM.
  */
public class CacheInvocation {
    private  static final ConcurrentHashMap<Long,Invocation> invocationMap=new ConcurrentHashMap<Long,Invocation>();
    public static Invocation getInvocation(long key){
        Invocation invocation=invocationMap.get(key);
        removeInvoction(key);
        return invocation;
    }
    public static void setInvocation(long key,Invocation invocation){
        invocationMap.put(key,invocation);
    }
    public static void removeInvoction(long key){
        invocationMap.remove(key);
    }
}
