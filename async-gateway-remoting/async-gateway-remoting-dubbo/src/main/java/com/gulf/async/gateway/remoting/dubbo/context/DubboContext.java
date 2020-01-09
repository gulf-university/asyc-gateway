package com.gulf.async.gateway.remoting.dubbo.context;

import com.gulf.async.gateway.spi.remote.RemotingContext;
import com.gulf.async.gateway.spi.remote.RemotingRequest;
import com.gulf.async.gateway.spi.remote.RemotingResponse;

/**
 * Created by xubai on 2020/01/08 7:35 PM.
 */
public class DubboContext implements RemotingContext {

    @Override
    public RemotingRequest request() {
        return null;
    }

    @Override
    public RemotingResponse response() {
        return null;
    }
}
