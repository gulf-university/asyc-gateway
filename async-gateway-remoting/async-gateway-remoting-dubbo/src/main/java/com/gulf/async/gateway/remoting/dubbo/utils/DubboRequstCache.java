package com.gulf.async.gateway.remoting.dubbo.utils;

import com.gulf.async.gateway.remoting.dubbo.context.DubboProtocol;
import com.gulf.async.gateway.remoting.dubbo.context.DubboRequest;

import java.util.concurrent.ConcurrentHashMap;


public class DubboRequstCache {

    protected static final ConcurrentHashMap<Long /* opaque */, DubboRequest> rpcRequstTable = new ConcurrentHashMap<Long, DubboRequest>();

    public static void addRpcRequst(long opaque, DubboRequest remotingCommand){
        rpcRequstTable.putIfAbsent(opaque, remotingCommand);
    }
    public static DubboRequest getRpcRequst(long opaque){
        DubboRequest remotingCommand=rpcRequstTable.get(opaque);
        //removeRpcRequst(opaque);
        return remotingCommand;
    }

    //todo:增加定时扫描器清除过时请求
    public static void removeRpcRequst(long opaque){
        rpcRequstTable.remove(opaque);
    }
}
