package com.gulf.async.gateway.remoting.http.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.FullHttpRequest;

import java.util.List;

/**
 * Created by xubai on 2019/10/16 12:26 PM.
 */
public class HttpJsonRequestDecoder extends MessageToMessageDecoder<FullHttpRequest> {

    @Override
    protected void decode(ChannelHandlerContext ctx, FullHttpRequest msg, List<Object> out) throws Exception {

    }
}
