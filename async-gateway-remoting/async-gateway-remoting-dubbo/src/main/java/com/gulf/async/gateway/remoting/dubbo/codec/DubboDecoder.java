package com.gulf.async.gateway.remoting.dubbo.codec;

import com.gulf.async.gateway.common.log.Logger;
import com.gulf.async.gateway.common.log.LoggerFactory;
import com.gulf.async.gateway.common.util.SystemPropertyUtil;
import com.gulf.async.gateway.remoting.api.utils.RemotingUtil;
import com.gulf.async.gateway.remoting.dubbo.context.DubboResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.nio.ByteBuffer;


public class DubboDecoder extends LengthFieldBasedFrameDecoder {

    private static final Logger LOG = LoggerFactory.getInstance(DubboDecoder.class);

    private static  int FRAME_MAX_LENGTH = SystemPropertyUtil.getInt("gateway.dubbo_frame_maxLength", 268435455);

    public DubboDecoder() {
        super(FRAME_MAX_LENGTH, 12, 4, 0, 0);
    }

    public Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf frame = null;
        try {
            frame = (ByteBuf) super.decode(ctx, in);
            if (null == frame) {
                return null;
            }

            ByteBuffer byteBuffer = frame.nioBuffer();


            return DubboResponse.decode(byteBuffer);
        }
        catch (Exception e) {
            String remoteAddress = RemotingUtil.parseRemoteAddress(ctx.channel());
            LOG.error("decode exception, " + remoteAddress, e);
            ctx.channel().close().addListener(new ChannelFutureListener() {

                public void operationComplete(ChannelFuture future) throws Exception {
                    LOG.info("closeChannel: close the connection to remote address[{}] result: {}", remoteAddress, future.isSuccess());
                }
            });
        }
        finally {
            if (null != frame) {
                frame.release();
            }
        }

        return null;
    }

}
