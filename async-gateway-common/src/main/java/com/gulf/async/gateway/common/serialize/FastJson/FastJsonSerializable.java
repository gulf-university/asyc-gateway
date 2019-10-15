package com.gulf.async.gateway.common.serialize.FastJson;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;

/**
 * Created by xubai on 2018/11/07 5:30 PM.
  */
public class FastJsonSerializable {
    private static final Logger log = LoggerFactory.getLogger(FastJsonSerializable.class);

    public static String toJson(final Object obj) {
        return JSON.toJSONString(obj, SerializerFeature.WriteMapNullValue, SerializerFeature.PrettyFormat, SerializerFeature.DisableCircularReferenceDetect, SerializerFeature.WriteNonStringKeyAsString);
    }

    public static <T> T fromJson(String json, Class<T> classOfT) {
        return JSON.parseObject(json, classOfT);
    }



    public static byte[] encode(final Object obj) {
        final String json = toJson(obj);
        log.debug(json);
        if (json != null) {
            return json.getBytes(Charset.forName("UTF-8"));
        }
        return null;
    }


    public static <T> T decode(final byte[] data, Class<T> classOfT) {
        final String json = new String(data, Charset.forName("UTF-8"));
        log.debug(json);
        return fromJson(json, classOfT);
    }
}
