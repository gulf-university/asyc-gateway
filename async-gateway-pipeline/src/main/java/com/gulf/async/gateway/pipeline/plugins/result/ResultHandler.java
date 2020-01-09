package com.gulf.async.gateway.pipeline.plugins.result;

import com.gulf.async.gateway.common.serialize.FastJson.FastJsonSerializable;
import com.gulf.async.gateway.common.spi.Activate;
import com.gulf.async.gateway.pipeline.AbstractGatewayPlugin;
import com.gulf.async.gateway.remoting.dubbo.context.DubboRequest;
import com.gulf.async.gateway.remoting.dubbo.utils.DubboRequstCache;
import com.gulf.async.gateway.remoting.dubbo.utils.Response;
import com.gulf.async.gateway.remoting.http.HttpResponse;
import com.gulf.async.gateway.remoting.spi.connection.Connection;
import com.gulf.async.gateway.remoting.spi.connection.ConnectionManager;
import com.gulf.async.gateway.spi.ApiResult;
import com.gulf.async.gateway.spi.pipeline.GatewayPlugin;
import com.gulf.async.gateway.spi.remote.RemotingContext;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.FullHttpResponse;

/**
 * Created by xubai on 2020/01/09 10:30 AM.
 */
@Activate(group = {GatewayPlugin.PLUGIN_GROUP}, sort = Integer.MAX_VALUE-1)
public class ResultHandler extends AbstractGatewayPlugin {

    //TODO
    private static ConnectionManager<Connection> httpConnectionManager;

    @Override
    public void post(RemotingContext context) {
        Response dubboResponse = (Response)context.response().getResponse();
        DubboRequest request = DubboRequstCache.getRpcRequst(dubboResponse.getId());
        Connection httpConnection = httpConnectionManager.get(request.getHttpRequestId());
        if (httpConnection == null){
            return;
        }
        Channel channel= (Channel)httpConnection.target();
        if (channel == null || !channel.isActive()) {
            return;
        }
        //2.回写http connection
        try {
            byte[] responseBytes = null;
            if(dubboResponse.getStatus()== Response.CLIENT_ERROR){
                ApiResult muResult=new ApiResult();
                muResult.setCode(502);
                muResult.setMessage(dubboResponse.getErrorMessage());
                responseBytes =  FastJsonSerializable.encode(muResult);
            } else if(dubboResponse.getResult() == null){
                ApiResult muResult=new ApiResult();
                muResult.setCode(503);
                muResult.setMessage(dubboResponse.getErrorMessage());
                responseBytes =  FastJsonSerializable.encode(muResult);
            }else {
                responseBytes = FastJsonSerializable.encode(dubboResponse.getResult());
            }
            HttpResponse httpResponse = new HttpResponse(responseBytes);
            FullHttpResponse r = HttpResponse.createFullHttpResponse(httpResponse);
            channel.writeAndFlush(r);
        } finally {
            httpConnectionManager.remove(request.getHttpRequestId());
        }
    }
}
