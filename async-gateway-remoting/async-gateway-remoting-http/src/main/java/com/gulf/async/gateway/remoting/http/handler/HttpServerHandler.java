package com.gulf.async.gateway.remoting.http.handler;

import com.alibaba.fastjson.JSON;
import com.gulf.async.gateway.common.Constants;
import com.gulf.async.gateway.common.spi.SpiLoader;
import com.gulf.async.gateway.spi.pipeline.GatewayPipeline;
import com.gulf.async.gateway.remoting.http.HttpContext;
import com.gulf.async.gateway.remoting.http.HttpRequest;
import com.gulf.async.gateway.remoting.http.HttpResponse;
import com.gulf.async.gateway.spi.remote.RemotingContext;
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

    private GatewayPipeline pipeline;

    public HttpServerHandler() {
        pipeline = SpiLoader.getSpiLoader(GatewayPipeline.class).getAdativateSpi("pipeline", "default").get(0);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        // 校验reqeust
        if (!request.decoderResult().isSuccess()) {
            sendError(ctx, "", HttpResponseStatus.BAD_REQUEST);
            return;
        }
        if (request.method() != HttpMethod.POST) {
            sendError(ctx, "gateway only support post!", HttpResponseStatus.METHOD_NOT_ALLOWED);
            return;
        }
        if (!request.headers().get(HttpHeaderNames.CONTENT_TYPE).equals(Constants.CONTENT_TYPE)) {
            sendError(ctx, "gateway only support JSON,application/json", HttpResponseStatus.BAD_REQUEST);
            return;
        }
        //校验uri
        String uri = request.uri();
        if (!StringUtils.startsWith(uri, Constants.GATEWAY_URI)) {
            sendError(ctx, uri + "gateway error uri!", HttpResponseStatus.NOT_FOUND);
            return;
        }
        //gateway 服务调用
        HttpRequest requestVo = null;
        RemotingContext httpContext = null;
        try {
            requestVo = JSON.parseObject(request.content().toString(Charset.forName(Constants.UTF_8)), HttpRequest.class);

        }catch (Throwable e){
            sendError(ctx, "gateway error request body JSON", HttpResponseStatus.BAD_REQUEST);
        }
        try{
            HttpContext.HttpRemotingRequest remotingRequest = new HttpContext.HttpRemotingRequest()
                    .setId(ctx.channel().id().toString())
                    .setServiceName(requestVo.getService()+"."+requestVo.getMethod())
                    .setServiceVersion(requestVo.getVersion())
                    .setServiceBody(requestVo.getBody());
            HttpContext.HttpRemotingResponse remotingResponse = new HttpContext.HttpRemotingResponse()
                    .setRequest(remotingRequest);
            httpContext = new HttpContext(remotingRequest, remotingResponse);
            pipeline.pre(httpContext);
        }catch (Throwable e){
            sendError(ctx, "gateway internel error", HttpResponseStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void sendError(ChannelHandlerContext ctx, String result, HttpResponseStatus status) {
        try {
            FullHttpResponse r1 = HttpResponse.createFullHttpResponse(
                    new HttpResponse(result.getBytes(Constants.UTF_8)), status, HttpResponse.CONTENT_TYPE_TEXT);
            ctx.channel().writeAndFlush(r1).addListener(ChannelFutureListener.CLOSE);
        } catch (Throwable e) {
            ctx.close();
        }
    }
}
