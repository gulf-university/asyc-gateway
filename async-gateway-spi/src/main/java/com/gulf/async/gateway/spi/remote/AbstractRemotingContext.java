package com.gulf.async.gateway.spi.remote;

/**
 * Created by xubai on 2020/01/08 9:46 PM.
 */
public abstract class AbstractRemotingContext implements RemotingContext {

    protected RemotingRequest request;
    protected RemotingResponse response;

    public AbstractRemotingContext(RemotingRequest request, RemotingResponse response) {
        this.request = request;
        this.response = response;
    }

    public void setRequest(RemotingRequest request) {
        this.request = request;
    }

    public void setResponse(RemotingResponse response) {
        this.response = response;
    }
}
