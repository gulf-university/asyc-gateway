package com.gulf.async.gateway.remoting.dubbo.codec;

import com.gulf.async.gateway.remoting.api.utils.RemotingUtil;
import com.gulf.async.gateway.remoting.dubbo.context.DubboRequest;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by xubai on 2018/11/07 5:30 PM.
  */
public class DubboEncoder extends MessageToByteEncoder<DubboRequest> {

    private static final Logger log = LoggerFactory.getLogger(DubboEncoder.class);

    protected void encode(ChannelHandlerContext ctx, DubboRequest dubboRequest, ByteBuf out) throws Exception {
        try {
            if(dubboRequest.getBodyBuf()!=null)
                out.writeBytes(dubboRequest.getBodyBuf());
        }
        catch (Exception e) {
            log.error("encode exception, " + RemotingUtil.parseRemoteAddress(ctx.channel()), e);
            if (dubboRequest != null) {
                log.error(dubboRequest.toString());
            }

            RemotingUtil.closeChannel(ctx.channel());
        }
        finally {
            if(dubboRequest.getBodyBuf()!=null){
                dubboRequest.getBodyBuf().release();
            }
        }
    }
}
