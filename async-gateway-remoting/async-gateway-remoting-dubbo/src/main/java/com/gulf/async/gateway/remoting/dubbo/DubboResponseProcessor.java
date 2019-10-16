package com.gulf.async.gateway.remoting.dubbo;

import com.gulf.async.gateway.common.Constants;
import com.gulf.async.gateway.common.serialize.FastJson.FastJsonSerializable;
import com.gulf.async.gateway.remoting.api.connection.Connection;
import com.gulf.async.gateway.remoting.api.connection.ConnectionManager;
import com.gulf.async.gateway.remoting.api.context.RemotingResponse;
import com.gulf.async.gateway.remoting.dubbo.context.DubboRequest;
import com.gulf.async.gateway.remoting.dubbo.utils.DubboRequstCache;
import com.gulf.async.gateway.remoting.dubbo.utils.Response;
import com.gulf.async.gateway.spi.ApiResult;
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

    public static void process(Response response){
        DubboRequest request = DubboRequstCache.getRpcRequst(response.getId());
        Connection httpConnection = httpConnectionManager.get(request.getHttpRequestId());
        if (httpConnection == null){
            return;
        }
        //2.回写http connection
        try {
            byte[] responseBytes = null;
            Channel channel= (Channel)httpConnection.target();
            if(response.getStatus()==Response.CLIENT_ERROR){
                ApiResult muResult=new ApiResult();
                muResult.setCode(502);
                muResult.setMessage(response.getErrorMessage());
                responseBytes =  FastJsonSerializable.encode(muResult);
            } else if(response.getResult() == null){
                ApiResult muResult=new ApiResult();
                muResult.setCode(503);
                muResult.setMessage(response.getErrorMessage());
                responseBytes =  FastJsonSerializable.encode(muResult);
            }else {
                responseBytes = FastJsonSerializable.encode(response.getResult());
            }
            if (channel != null && channel.isActive()) {
                FullHttpResponse r = createFullHttpResponse(responseBytes);
                channel.writeAndFlush(r);
            }
        } finally {
            httpConnectionManager.remove(request.getHttpRequestId());
        }
    }

    public static FullHttpResponse createFullHttpResponse(byte[] data){
        ByteBuf content = Unpooled.copiedBuffer(data);
        int contentLength = data.length;
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, content);
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, contentLength);
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, Constants.CONTENT_TYPE_C);
        return response;
    }

}
