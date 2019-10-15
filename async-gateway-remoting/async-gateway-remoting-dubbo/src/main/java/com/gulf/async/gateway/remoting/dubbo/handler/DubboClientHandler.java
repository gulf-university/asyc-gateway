package com.gulf.async.gateway.remoting.dubbo.handler;

import com.gulf.async.gateway.remoting.dubbo.context.DubboResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Created by xubai on 2019/10/15 10:00 PM.
 */
public class DubboClientHandler extends SimpleChannelInboundHandler<DubboResponse> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DubboResponse msg) throws Exception {

    }
}
