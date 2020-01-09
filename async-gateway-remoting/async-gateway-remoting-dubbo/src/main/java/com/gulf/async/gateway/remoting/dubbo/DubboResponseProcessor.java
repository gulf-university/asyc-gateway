package com.gulf.async.gateway.remoting.dubbo;

import com.gulf.async.gateway.common.Constants;
import com.gulf.async.gateway.common.serialize.FastJson.FastJsonSerializable;
import com.gulf.async.gateway.remoting.dubbo.context.DubboContext;
import com.gulf.async.gateway.remoting.spi.connection.Connection;
import com.gulf.async.gateway.remoting.spi.connection.ConnectionManager;
import com.gulf.async.gateway.remoting.dubbo.context.DubboRequest;
import com.gulf.async.gateway.remoting.dubbo.utils.DubboRequstCache;
import com.gulf.async.gateway.remoting.dubbo.utils.Response;
import com.gulf.async.gateway.spi.ApiResult;
import com.gulf.async.gateway.spi.pipeline.GatewayPipeline;
import com.gulf.async.gateway.spi.remote.AbstractRemotingContext;
import com.gulf.async.gateway.spi.remote.RemotingContext;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.*;

/**
 * Created by xubai on 2019/10/16 5:35 PM.
 */
public class DubboResponseProcessor {

    //TODO
    private static ConnectionManager<Connection> httpConnectionManager;
    //TODO
    private static GatewayPipeline pipeline;

    public static void process(Response response){
        DubboRequest request = DubboRequstCache.getRpcRequst(response.getId());
        Connection httpConnection = httpConnectionManager.get(request.getHttpRequestId());
        if (httpConnection == null){
            return;
        }
        //1.处理pipeline
        AbstractRemotingContext httpContext = (AbstractRemotingContext) pipeline.getSource(httpConnection.id());
        httpContext.response().setResponse(response);
        pipeline.post(httpContext);
    }


}
