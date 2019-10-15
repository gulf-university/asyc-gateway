package com.gulf.async.gateway.remoting.http;

import com.gulf.async.gateway.remoting.api.context.RemotingContext;
import com.gulf.async.gateway.remoting.api.context.RemotingRequest;
import com.gulf.async.gateway.remoting.api.context.RemotingResponse;

/**
 * Created by xubai on 2018/11/07 7:25 PM.
 */
public class HttpContext implements RemotingContext {

    @Override
    public RemotingRequest request() {
        return null;
    }

    @Override
    public RemotingResponse response() {
        return null;
    }
}
