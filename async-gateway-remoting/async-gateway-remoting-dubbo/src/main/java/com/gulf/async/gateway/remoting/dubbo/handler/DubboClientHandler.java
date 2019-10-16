package com.gulf.async.gateway.remoting.dubbo.handler;

import com.gulf.async.gateway.common.Constants;
import com.gulf.async.gateway.common.log.Logger;
import com.gulf.async.gateway.common.log.LoggerFactory;
import com.gulf.async.gateway.common.thread.NamedThreadFactory;
import com.gulf.async.gateway.common.util.SystemPropertyUtil;
import com.gulf.async.gateway.remoting.api.utils.RemotingUtil;
import com.gulf.async.gateway.remoting.dubbo.DubboResponseProcessor;
import com.gulf.async.gateway.remoting.dubbo.context.DubboRequest;
import com.gulf.async.gateway.remoting.dubbo.context.DubboResponse;
import com.gulf.async.gateway.remoting.dubbo.utils.ExchangeCodec;
import com.gulf.async.gateway.remoting.dubbo.utils.Response;
import com.gulf.async.gateway.spi.ApiResult;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by xubai on 2019/10/15 10:00 PM.
 */
public class DubboClientHandler extends SimpleChannelInboundHandler<DubboResponse> {

    private final static Logger LOG = LoggerFactory.getInstance(DubboClientHandler.class);

    private ThreadPoolExecutor executorService = null;

    public DubboClientHandler(ThreadPoolExecutor executorService) {
        this.executorService = executorService;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DubboResponse msg) throws Exception {
        if (msg == null){
            return;
        }
        executorService.execute(() -> {
            try {
                Response res = (Response) ExchangeCodec.decode(msg.getBodyBytes(), msg.getReadable(),msg.getHeader());
                if(res==null)return;
                //1.心跳需要返回给provider
                if(res.isHeartbeat()){
                    DubboRequest requestCommandRpc = DubboRequest.createRequestCommand(0);
                    ByteBuf byteBuf= ByteBufAllocator.DEFAULT.buffer();
                    ExchangeCodec.encodeResponse(byteBuf, res);
                    requestCommandRpc.setBodyBuf(byteBuf);
                    ctx.writeAndFlush(requestCommandRpc);
                    return;
                }
                DubboResponseProcessor.process(res);
            } catch (Exception e) {
                //处理失败将不再处理
                LOG.warn(RemotingUtil.parseRemoteAddress(ctx.channel())
                        + ", too many requests and system thread pool busy, RejectedExecutionException, request id: " + msg.getRequestId());
            }
        });
    }
}
