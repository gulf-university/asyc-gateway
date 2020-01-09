package com.gulf.async.gateway.remoting.http;

import com.gulf.async.gateway.spi.remote.AbstractRemotingContext;
import com.gulf.async.gateway.spi.remote.RemotingContext;
import com.gulf.async.gateway.spi.remote.RemotingRequest;
import com.gulf.async.gateway.spi.remote.RemotingResponse;

/**
 * Created by xubai on 2018/11/07 7:25 PM.
 */
public class HttpContext extends AbstractRemotingContext {


    public HttpContext(RemotingRequest request, RemotingResponse response) {
        super(request, response);
    }

    @Override
    public RemotingRequest request() {
        return this.request;
    }

    @Override
    public RemotingResponse response() {
        return this.response;
    }


    /**
     * http request wrapper
     */
    public static class HttpRemotingRequest extends RemotingRequest{

        private String id;

        private String serviceName;
        private String serviceVersion;
        private Object serviceBody;



        @Override
        public String id() {
            return id;
        }

        public HttpRemotingRequest setId(String id) {
            this.id = id;
            return this;
        }

        public HttpRemotingRequest setServiceName(String serviceName) {
            this.serviceName = serviceName;
            return this;
        }

        public HttpRemotingRequest setServiceVersion(String serviceVersion) {
            this.serviceVersion = serviceVersion;
            return this;
        }

        public HttpRemotingRequest setServiceBody(Object serviceBody) {
            this.serviceBody = serviceBody;
            return this;
        }
    }


    /**
     * http response wrapper
     */
    public static class HttpRemotingResponse extends RemotingResponse{

        private RemotingRequest request;



        @Override
        public String requestId() {
            return request.id();
        }

        public HttpRemotingResponse setRequest(RemotingRequest request) {
            this.request = request;
            return this;
        }

    }
}
