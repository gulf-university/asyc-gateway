package com.gulf.async.gateway.remoting.dubbo.utils;

import com.gulf.async.gateway.common.serialize.ObjectInput;
import com.gulf.async.gateway.common.serialize.dubbo.DubboSerialization;
import com.gulf.async.gateway.common.util.ReflectUtils;
import com.gulf.async.gateway.common.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by xubai on 2018/11/07 5:30 PM.
  */
public class DecodeableRpcInvocation extends RpcInvocation {

    private static final Logger log= LoggerFactory.getLogger(DecodeableRpcInvocation.class);

    private byte        serializationType;

    private InputStream inputStream;

    private Request     request;

    private volatile boolean hasDecoded;

    public DecodeableRpcInvocation(Request request, InputStream is, byte id) {
        this.request = request;
        this.inputStream = is;
        this.serializationType = id;
    }

    public void decode() throws Exception {
        if (!hasDecoded && inputStream != null) {
            try {
                decode( inputStream);
            } catch (Throwable e) {
                if (log.isWarnEnabled()) {
                    log.warn("Decode rpc invocation failed: " + e.getMessage(), e);
                }
                request.setBroken(true);
                request.setData(e);
            } finally {
                hasDecoded = true;
            }
        }
    }


    public Object decode( InputStream input) throws IOException {
        ObjectInput in = new DubboSerialization().deserialize(input);

        setAttachment(DubboConstants.DUBBO_VERSION_KEY, in.readUTF());
        setAttachment(DubboConstants.PATH_KEY, in.readUTF());
        setAttachment(DubboConstants.VERSION_KEY, in.readUTF());

        setMethodName(in.readUTF());
        try {
            Object[] args;
            Class<?>[] pts;
            String desc = in.readUTF();
            if (desc.length() == 0) {
                pts = ExchangeCodec.EMPTY_CLASS_ARRAY;
                args = ExchangeCodec.EMPTY_OBJECT_ARRAY;
            } else {
                pts = ReflectUtils.desc2classArray(desc);
                args = new Object[pts.length];
                for (int i = 0; i < args.length; i++) {
                    try {
                        args[i] = in.readObject(pts[i]);
                    } catch (Exception e) {
                        if (log.isWarnEnabled()) {
                            log.warn("Decode argument failed: " + e.getMessage(), e);
                        }
                    }
                }
            }
            setParameterTypes(pts);

            Map<String, String> map = (Map<String, String>) in.readObject(Map.class);
            if (map != null && map.size() > 0) {
                Map<String, String> attachment = getAttachments();
                if (attachment == null) {
                    attachment = new HashMap<String, String>();
                }
                attachment.putAll(map);
                setAttachments(attachment);
            }

            //decode argument ,may be callback
//            for (int i = 0; i < args.length; i++) {
//                args[i] = decodeInvocationArgument( this, pts, i, args[i]);
//            }
            setArguments(args);

        } catch (ClassNotFoundException e) {
            throw new IOException(StringUtil.toString("Read invocation data failed.", e));
        }
        return this;
    }
}
