package com.gulf.async.gateway.remoting.dubbo.utils;

import com.gulf.async.gateway.common.serialize.ObjectInput;
import com.gulf.async.gateway.common.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class DecodeableRpcResult extends RpcResult {

    private static final Logger log = LoggerFactory.getLogger(DecodeableRpcResult.class);

    private byte        serializationType;

    private ObjectInput inputStream;

    private Response    response;

//    private Invocation invocation;
    private InputStream iis;

    private volatile boolean hasDecoded;

    public DecodeableRpcResult(Response response, ObjectInput is, byte id, InputStream iis) {
        this.response = response;
        this.inputStream = is;
//        this.invocation = invocation;
        this.serializationType = id;
        this.iis=iis;
    }

    public void encode( OutputStream output, Object message) throws IOException {
        throw new UnsupportedOperationException();
    }

    public Object decode( ObjectInput input) throws IOException {
        ObjectInput in = input;

        byte flag = in.readByte();

        switch (flag) {
            case ExchangeCodec.RESPONSE_NULL_VALUE:
                break;
            case ExchangeCodec.RESPONSE_VALUE:
//                try {
//                    Type[] returnType = invocation.getReturnTypes();
//
//                    setValue(returnType == null || returnType.length == 0 ? in.readObject() : (returnType.length == 1 ? in.readObject((Class<?>) returnType[0])
//                            : in.readObject((Class<?>) returnType[0], returnType[1])));

                     //setValue(in.readBytes());
//                } catch (ClassNotFoundException e) {
//                    throw new IOException(StringUtils.toString("Read response data failed.", e));
//                }
                try {
                    setValue(in.readObject());
                } catch (ClassNotFoundException e) {
                    throw new IOException(StringUtil.toString("Read response data failed.", e));
                }
                break;
            case ExchangeCodec.RESPONSE_WITH_EXCEPTION:
                try {
                    Object obj = in.readObject();
                    if (obj instanceof Throwable == false)
                        throw new IOException("Response data error, expect Throwable, but get " + obj);
                    setException((Throwable) obj);
                } catch (ClassNotFoundException e) {
                    throw new IOException(StringUtil.toString("Read response data failed.", e));
                }
                break;
            default:
                throw new IOException("Unknown result flag, expect '0' '1' '2', get " + flag);
        }
        return this;
    }

    public void decode() throws Exception {
        if (!hasDecoded  && inputStream != null) {
            try {
                decode( inputStream);
            } catch (Throwable e) {
                if (log.isWarnEnabled()) {
                    log.warn("Decode rpc result failed: " + e.getMessage(), e);
                }
                response.setStatus(Response.CLIENT_ERROR);
                response.setErrorMessage(StringUtil.toString(e));
            } finally {
                hasDecoded = true;
            }
        }
    }
}
