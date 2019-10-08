package com.gulf.async.gateway.remoting.http.handler;

import com.alibaba.fastjson.JSON;
import com.gulf.async.gateway.common.Constants;
import com.gulf.async.gateway.common.util.StringUtil;
import com.gulf.async.gateway.remoting.http.HttpRequest;
import com.gulf.async.gateway.remoting.http.HttpResponse;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.Charset;

/**
 * Created by xubai on 2019/09/25 3:42 PM.
 */
public class HttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        // 校验reqeust
        if (!request.decoderResult().isSuccess()) {
            sendError(ctx, "", HttpResponseStatus.BAD_REQUEST);
            return;
        }
        if (request.method() != HttpMethod.POST) {
            sendError(ctx, "只支持POST请求!", HttpResponseStatus.METHOD_NOT_ALLOWED);
            return;
        }
        if (!request.headers().get(HttpHeaderNames.CONTENT_TYPE).equals(Constants.CONTENT_TYPE)) {
            sendError(ctx, "只支持JSON,application/json", HttpResponseStatus.BAD_REQUEST);
            return;
        }
        //校验uri
        String uri = request.uri();
        if (!StringUtils.startsWith(uri, Constants.GATEWAY_URI)) {
            sendError(ctx, uri + "错误的请求地址!", HttpResponseStatus.NOT_FOUND);
            return;
        }
        //gateway 服务调用
        HttpRequest requestVo = null;
        try {
            requestVo = JSON.parseObject(request.content().toString(Charset.forName(Constants.UTF_8)), HttpRequest.class);
            //TODO pipeline chain

        }catch (Throwable e){
            sendError(ctx, "错误的JSON格式", HttpResponseStatus.BAD_REQUEST);
        }
    }

    private void sendError(ChannelHandlerContext ctx, String result, HttpResponseStatus status) {
        try {
            FullHttpResponse r1 = HttpResponse.createFullHttpResponse(new HttpResponse(result.getBytes(Constants.UTF_8)), status);
            ctx.channel().writeAndFlush(r1).addListener(ChannelFutureListener.CLOSE);
        } catch (Throwable e) {
            ctx.close();
        }
    }
}
