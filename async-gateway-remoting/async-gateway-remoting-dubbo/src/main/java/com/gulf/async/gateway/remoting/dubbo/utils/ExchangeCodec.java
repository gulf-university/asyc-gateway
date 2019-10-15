package com.gulf.async.gateway.remoting.dubbo.utils;

import com.gulf.async.gateway.common.log.Logger;
import com.gulf.async.gateway.common.log.LoggerFactory;
import com.gulf.async.gateway.common.serialize.ObjectInput;
import com.gulf.async.gateway.common.serialize.ObjectOutput;
import com.gulf.async.gateway.common.serialize.Serialization;
import com.gulf.async.gateway.common.serialize.dubbo.DubboSerialization;
import com.gulf.async.gateway.common.serialize.dubbo.io.StreamUtils;
import com.gulf.async.gateway.common.serialize.hession.Hessian2Serialization;
import com.gulf.async.gateway.common.util.Bytes;
import com.gulf.async.gateway.common.util.ReflectUtils;
import com.gulf.async.gateway.common.util.StringUtil;
import io.netty.buffer.ByteBuf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ExchangeCodec {

    private static final Logger logger = LoggerFactory.getInstance(ExchangeCodec.class);
    // header length.
    public static final int      HEADER_LENGTH      = 16;

    // magic header.
    protected static final short    MAGIC              = (short) 0xdabb;

    protected static final byte     MAGIC_HIGH         = Bytes.short2bytes(MAGIC)[0];

    protected static final byte     MAGIC_LOW          = Bytes.short2bytes(MAGIC)[1];

    // message flag.
    protected static final byte     FLAG_REQUEST       = (byte) 0x80;

    protected static final byte     FLAG_TWOWAY        = (byte) 0x40;

    protected static final byte     FLAG_EVENT     = (byte) 0x20;

    protected static final int      SERIALIZATION_MASK = 0x1f;

    public static final byte RESPONSE_NULL_VALUE = 2;
    public static final byte RESPONSE_VALUE = 1;
    public static final byte RESPONSE_WITH_EXCEPTION = 0;

    public static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];
    public static final Class<?>[] EMPTY_CLASS_ARRAY = new Class<?>[0];


    enum DecodeResult {
        NEED_MORE_INPUT, SKIP_SOME_INPUT
    }
    public Short getMagicCode() {
        return MAGIC;
    }

    public static Object decode( InputStream is, int readable, byte[] header) throws IOException {
        // check magic number.
        if (readable > 0 && header[0] != MAGIC_HIGH
                || readable > 1 && header[1] != MAGIC_LOW) {
            return "";
        }
        // check length.
        if (readable < HEADER_LENGTH) {
            return DecodeResult.NEED_MORE_INPUT;
        }

        // get data length.
        int len = Bytes.bytes2int(header, 12);

        int tt = len + HEADER_LENGTH;
        if( readable < tt ) {
            return DecodeResult.NEED_MORE_INPUT;
        }

        // limit input stream.¶


        try {
            return decodeBody(is, header);
        } finally {
            if (is.available() > 0) {
                try {
                    if (logger.isWarnEnabled()) {
                        logger.warn("Skip input stream " + is.available());
                    }
                    StreamUtils.skipUnusedStream(is);
                    is.close();
                } catch (IOException e) {
                    logger.warn(e.getMessage(), e);
                }
            }
        }
    }

    protected static Object decodeBody(InputStream is, byte[] header) throws IOException {
        byte flag = header[2], proto = (byte) (flag & SERIALIZATION_MASK);
        Serialization s = new Hessian2Serialization();
        // get request id.
        long id = Bytes.bytes2long(header, 4);
        if ((flag & FLAG_REQUEST) == 0) {
            // decode response.
            Response res = new Response(id);
            if ((flag & FLAG_EVENT) != 0) {
                res.setEvent(Response.HEARTBEAT_EVENT);
            }
            // get status.
            byte status = header[3];
            res.setStatus(status);
            if (status == Response.OK) {
                try {
                    Object data;
                    if (res.isHeartbeat()) {
                        return null;
                    } else if (res.isEvent()) {
                        data = decodeEventData( s.deserialize(is));
                    } else {

                        DecodeableRpcResult result= new DecodeableRpcResult(res, s.deserialize(is), proto,is);
                        result.decode();
                        if(result.hasException()){
                            res.setStatus(Response.CLIENT_ERROR);

                            logger.error("505 service  error: " + result.getException().getMessage(),result.getException());

                            res.setErrorMessage("505 service error");
                        }

                        data = result.getValue();
                    }
                    res.setResult(data);
                } catch (Throwable t) {

                    logger.error("Decode response failed: " + t.getMessage(), t);

                    res.setStatus(Response.CLIENT_ERROR);
                    res.setErrorMessage(StringUtil.toString(t));
                }
            } else {
                String err=s.deserialize(is).readUTF();

                logger.error("506  request network error: " +err);

                res.setErrorMessage(err);
            }
            return res;
        } else {
            // decode request.
            Request req = new Request(id);
            req.setVersion("2.0.0");
            req.setTwoWay((flag & FLAG_TWOWAY) != 0);
            if ((flag & FLAG_EVENT) != 0) {
                req.setEvent(Request.HEARTBEAT_EVENT);
            }
            try {
                Object data=null;
                if (req.isHeartbeat()) {
                    Response res = new Response(id,"2.0.0");
                    res.setEvent(Response.HEARTBEAT_EVENT);
                    StreamUtils.skipUnusedStream(is);
                    return res;
                } else if (req.isEvent()) {
                    data = decodeEventData(s.deserialize(is));
                } else {
                    DecodeableRpcInvocation inv = new DecodeableRpcInvocation( req, is, proto);
                    inv.decode();
                    data = inv;
                }
                req.setData(data);
            } catch (Throwable t) {
                if (logger.isWarnEnabled()) {
                    logger.warn("Decode request failed: " + t.getMessage(), t);
                }
                // bad request
                req.setBroken(true);
                req.setData(t);
            }
            return req;
        }
    }


    /**
     * 2byte的魔数（标示Dubbo的协议）
     * 1byte的消息标志位
     *   5bit位的序列化ID
     *   1bit位的事件类型（区分心跳还是正常的请求/响应信息）
     *   1bit表示是oneWay还是twoWay请求
     *   1bit表示是request还是response
     * 1byte的状态位（标示Request和Response的状态）
     * 8byte的消息ID（requestId/responseId）
     * 4byte的数据长度
     *
     */
    public static void encodeRequest(ByteBuf buffer, Request req) throws IOException {
        Serialization serialization = new DubboSerialization();
        // header.
        byte[] header = new byte[HEADER_LENGTH];
        // set magic number.
        Bytes.short2bytes(MAGIC, header);

        // set request and serialization flag.
        header[2] = (byte) (FLAG_REQUEST | serialization.getContentTypeId());

        if (req.isTwoWay()) header[2] |= FLAG_TWOWAY;
        if (req.isEvent()) header[2] |= FLAG_EVENT;

        // set request id.
        Bytes.long2bytes(req.getId(), header, 4);

        // encode request data.
        //int savedWriteIndex = buffer.writerIndex();
        //buffer.writerIndex(savedWriteIndex + HEADER_LENGTH);
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();

        ObjectOutput out = serialization.serialize( byteArrayOutputStream);
        if (req.isEvent()) {
            //encodeEventData(out, req.getData());
        } else {
            encodeRequestData(out, req.getData());
        }
        out.flushBuffer();


        int len = byteArrayOutputStream.size();

        Bytes.int2bytes(len, header, 12);
        //short flag=1;
        //Bytes.short2bytes(flag, header, 16);

        // write
        //buffer.writerIndex(savedWriteIndex);
        buffer.writeBytes(header); // write header.
        buffer.writeBytes(byteArrayOutputStream.toByteArray());
    }
    private static void encodeRequestData(ObjectOutput out, Object data) throws IOException {
        RpcInvocation inv = (RpcInvocation) data;
        out.writeUTF("2.0.0");
        out.writeUTF(inv.getServiceClassName());
        out.writeUTF("1.0.0");
        out.writeUTF(inv.getMethodName());
        //out.writeBytes(inv.getRequestData());

        out.writeUTF(ReflectUtils.getDesc(inv.getParameterTypes()));

        Object[] args = inv.getArguments();
        if (args != null)
            for (int i = 0; i < args.length; i++){
                out.writeObject(args[i]);
            }

        out.writeObject(inv.getAttachments());
    }


    public static void encodeResponse(ByteBuf buffer, Response res) throws IOException {
        try {
            Serialization serialization = new DubboSerialization();
            // header.
            byte[] header = new byte[HEADER_LENGTH];
            // set magic number.
            Bytes.short2bytes(MAGIC, header);
            // set request and serialization flag.
            header[2] = serialization.getContentTypeId();
            if (res.isHeartbeat()) header[2] |= FLAG_EVENT;
            // set response status.
            byte status = res.getStatus();
            header[3] = status;
            // set request id.
            Bytes.long2bytes(res.getId(), header, 4);
            ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();

            ObjectOutput out = serialization.serialize( byteArrayOutputStream);
            // encode response data or error message.
            if (status == Response.OK) {
                if (res.isHeartbeat()) {
                    encodeHeartbeatData( out, null);
                } else {
                    encodeResponseData( out, res.getResult());
                }
            }
            else out.writeUTF(res.getErrorMessage());
            out.flushBuffer();

            int len = byteArrayOutputStream.size();

            Bytes.int2bytes(len, header, 12);
            short flag=0;
            Bytes.short2bytes(flag, header, 16);
            // write
//            buffer.writerIndex(savedWriteIndex);
            buffer.writeBytes(header); // write header.
            buffer.writeBytes(byteArrayOutputStream.toByteArray());
//            buffer.writerIndex(savedWriteIndex + HEADER_LENGTH + len);
        } catch (Throwable t) {

            if (! res.isEvent() && res.getStatus() != Response.BAD_RESPONSE) {
                    logger.warn("Fail to encode response: " + res + ", send bad_response info instead, cause: " + t.getMessage(), t);
                    Response r = new Response(res.getId(), res.getVersion());
                    r.setStatus(Response.BAD_RESPONSE);
                    return;
            }

            if (t instanceof IOException) {
                throw (IOException) t;
            } else if (t instanceof RuntimeException) {
                throw (RuntimeException) t;
            } else if (t instanceof Error) {
                throw (Error) t;
            } else  {
                throw new RuntimeException(t.getMessage(), t);
            }
        }
    }

    protected static void encodeResponseData(ObjectOutput out, Object data) throws IOException {
        Result result = (Result) data;

        Throwable th = result.getThrowable();
        if (th == null) {
            Object ret = result.getResult();
            if (ret == null) {
                out.writeByte(RESPONSE_NULL_VALUE);
            } else {
                out.writeByte(RESPONSE_VALUE);
                out.writeObject(ret);
            }
        } else {
            out.writeByte(RESPONSE_WITH_EXCEPTION);
            out.writeObject(th);
        }
    }

    private static void encodeEventData(ObjectOutput out, Object data) throws IOException {
        out.writeObject(data);
    }

    protected static void encodeHeartbeatData(ObjectOutput out, Object data) throws IOException {
        encodeEventData(out, data);
    }


    protected static Object decodeEventData( ObjectInput in) throws IOException {
        try {
            return in.readObject();
        } catch (ClassNotFoundException e) {
            throw new IOException(StringUtil.toString("Read object failed.", e));
        }
    }

    protected static Object getRequestData(long id) {
       return CacheInvocation.getInvocation(id);
    }
}
