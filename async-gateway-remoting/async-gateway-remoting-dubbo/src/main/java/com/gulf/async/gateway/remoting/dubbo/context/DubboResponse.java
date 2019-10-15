package com.gulf.async.gateway.remoting.dubbo.context;

import com.gulf.async.gateway.common.serialize.dubbo.buff.ChannelBuffer;
import com.gulf.async.gateway.common.util.Bytes;
import com.gulf.async.gateway.remoting.dubbo.utils.ExchangeCodec;
import com.gulf.async.gateway.remoting.dubbo.utils.NettyBackedChannelBufferFactory;
import com.gulf.async.gateway.remoting.dubbo.utils.RpcRequstCache;

import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;

/**
 * Created by xubai on 2019/10/11 6:06 PM.
 */
public class DubboResponse extends DubboProtocol {


    public static DubboProtocol decode(final ByteBuffer byteBuffer){
        ChannelBuffer buffer= NettyBackedChannelBufferFactory.getInstance().getBuffer(byteBuffer);
        int readable = buffer.readableBytes();

        byte[] header = new byte[Math.min(readable, ExchangeCodec.HEADER_LENGTH)];
        buffer.readBytes(header);

        byte[] body = new byte[buffer.readableBytes()];
        buffer.readBytes(body);
        buffer.clear();
        byteBuffer.clear();
        byteBuffer.flip();

        DubboProtocol responseCommand;

        if(readable==19){
            responseCommand=createHeartbeat(DubboProtocol.DubboResponseProcessCode);
        }else {
            //responseCommand= RpcRequstCache.getRpcRequst( Bytes.bytes2long(header, 4));
            responseCommand= createResponseCommand(RpcRequstCache.getRpcRequst( Bytes.bytes2long(header, 4)));
        }

        responseCommand.setReadable(readable);

        responseCommand.setBodyBytes(new ByteArrayInputStream(body));

        responseCommand.setHeader(header);

        return responseCommand;
    }

    public static DubboRequest createHeartbeat(int code){
        return  DubboRequest.createRequestCommand(code);
    }

    //需要增加remark序列化并赋值为body
    public static DubboResponse createResponseCommand(DubboRequest request ) {
        DubboResponse cmd = new DubboResponse();
        cmd.markResponseType();

        cmd.setHeader(request.getHeader());

        return cmd;
    }

}
