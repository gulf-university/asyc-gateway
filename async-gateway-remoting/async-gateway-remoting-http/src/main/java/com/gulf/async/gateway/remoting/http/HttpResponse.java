package com.gulf.async.gateway.remoting.http;

import com.gulf.async.gateway.common.Constants;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;

import java.io.UnsupportedEncodingException;

/**
 * Created by xubai on 2018/11/07 7:25 PM.
 */
public class HttpResponse {

    public final static String CONTENT_TYPE = "application/json; charset=utf-8";

    public HttpResponse(String bodyStr) {
        try {
            this.body = bodyStr.getBytes(Constants.UTF_8);
        } catch (Throwable e) {
            //NOOP
        }
    }

    public HttpResponse(byte[] body) {
        this.body = body;
    }

    private byte[] body;

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public static FullHttpResponse createFullHttpResponse(HttpResponse httpResponse){
        return createFullHttpResponse(httpResponse, HttpResponseStatus.OK);
    }

    public static FullHttpResponse createFullHttpResponse(HttpResponse httpResponse, HttpResponseStatus status){
        ByteBuf content = Unpooled.copiedBuffer(httpResponse.getBody());
        int contentLength = httpResponse.getBody().length;
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, content);
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, contentLength);
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, CONTENT_TYPE);
        return response;
    }
}
