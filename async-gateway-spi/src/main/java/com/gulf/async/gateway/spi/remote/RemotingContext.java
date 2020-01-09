package com.gulf.async.gateway.spi.remote;

/**
 * Created by xubai on 2019/10/10 2:44 PM.
 */
public interface RemotingContext {

    RemotingRequest request();

    RemotingResponse response();

}
