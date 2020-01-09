package com.gulf.async.gateway.spi.remote;


/**
 * Created by xubai on 2019/10/10 2:44 PM.
 */
public abstract class RemotingResponse extends Protocol {

    protected Object response;

    public abstract String requestId();

    public void setResponse(Object response){
        this.response = response;
    }

    public Object getResponse() {
        return response;
    }
}
