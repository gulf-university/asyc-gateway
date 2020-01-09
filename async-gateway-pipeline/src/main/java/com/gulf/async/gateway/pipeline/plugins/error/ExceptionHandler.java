package com.gulf.async.gateway.pipeline.plugins.error;

import com.gulf.async.gateway.common.serialize.FastJson.FastJsonSerializable;
import com.gulf.async.gateway.common.spi.Activate;
import com.gulf.async.gateway.pipeline.AbstractGatewayPlugin;
import com.gulf.async.gateway.remoting.dubbo.utils.Response;
import com.gulf.async.gateway.remoting.http.HttpResponse;
import com.gulf.async.gateway.remoting.spi.connection.Connection;
import com.gulf.async.gateway.remoting.spi.connection.ConnectionManager;
import com.gulf.async.gateway.spi.ApiResult;
import com.gulf.async.gateway.spi.pipeline.GatewayPlugin;
import com.gulf.async.gateway.spi.remote.RemotingContext;
import com.gulf.async.gateway.spi.remote.RemotingRequest;
import com.gulf.async.gateway.spi.remote.RemotingResponse;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.FullHttpResponse;

/**
 * Created by xubai on 2019/10/11 12:22 PM.
 */
@Activate(group = {GatewayPlugin.PLUGIN_GROUP}, sort = Integer.MAX_VALUE)
public class ExceptionHandler extends AbstractGatewayPlugin {

    //TODO
    private static ConnectionManager<Connection> httpConnectionManager;

    @Override
    public void post(RemotingContext context) {
        RemotingRequest remotingRequest = context.request();
        Connection httpConnection = httpConnectionManager.get(remotingRequest.id());
        if (httpConnection == null){
            return;
        }
        Channel channel= (Channel)httpConnection.target();
        if (channel == null || !channel.isActive()) {
            return;
        }
        Response dubboResponse = (Response)context.response().getResponse();
        if (dubboResponse.getStatus() != Response.OK){
            //TODO define gateway error code
            ApiResult muResult=new ApiResult();
            muResult.setCode(505);
            muResult.setMessage(dubboResponse.getErrorMessage());
            try {
                HttpResponse httpResponse = new HttpResponse(FastJsonSerializable.encode(muResult));
                FullHttpResponse r = HttpResponse.createFullHttpResponse(httpResponse);
                channel.writeAndFlush(r);
            } finally {
                httpConnectionManager.remove(remotingRequest.id());
            }
        }
    }
}
