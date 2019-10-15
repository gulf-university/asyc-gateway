package com.gulf.async.gateway.remoting.dubbo.context;

import com.gulf.async.gateway.common.util.SystemPropertyUtil;
import com.gulf.async.gateway.remoting.api.Protocol;
import io.netty.buffer.ByteBuf;

import java.io.ByteArrayInputStream;

/**
 * Created by xubai on 2019/10/15 11:48 AM.
 */
public class DubboProtocol extends Protocol {

    public static final int DubboResponseProcessCode =20;
    public static final int DubboRequestProcessCode=21;

    public static final int RPC_TYPE = 0; // 0, REQUEST_COMMAND

    public static final int RPC_ONEWAY = 1; // 0, RPC

    protected int flag = 0;

    protected long requestTimeOut=System.currentTimeMillis()+ SystemPropertyUtil.getLong("gateway.dubbo_request_timeout_millis", 3000);

    //dubbo部分
    protected transient int readable;
    protected transient ByteArrayInputStream bodyBytes;
    protected transient ByteBuf bodyBuf;
    protected transient long requestId;



    public void markResponseType() {
        int bits = 1 << RPC_TYPE;
        this.flag |= bits;
    }


    public boolean isResponseType() {
        int bits = 1 << RPC_TYPE;
        return (this.flag & bits) == bits;
    }

    public void markOnewayRPC() {
        int bits = 1 << RPC_ONEWAY;
        this.flag |= bits;
    }

    public boolean isOnewayRPC() {
        int bits = 1 << RPC_ONEWAY;
        return (this.flag & bits) == bits;
    }

    public boolean isTimeout(){
        return requestTimeOut<System.currentTimeMillis();
    }

    public int getReadable() {
        return readable;
    }

    public void setReadable(int readable) {
        this.readable = readable;
    }

    public ByteArrayInputStream getBodyBytes() {
        return bodyBytes;
    }

    public void setBodyBytes(ByteArrayInputStream bodyBytes) {
        this.bodyBytes = bodyBytes;
    }

    public ByteBuf getBodyBuf() {
        return bodyBuf;
    }

    public void setBodyBuf(ByteBuf bodyBuff) {
        if(this.bodyBuf!=null)
            this.bodyBuf.release();

        this.bodyBuf = bodyBuf;
    }

    public long getRequestId() {
        return requestId;
    }

    public void setRequestId(long requestId) {
        this.requestId = requestId;
    }



}
