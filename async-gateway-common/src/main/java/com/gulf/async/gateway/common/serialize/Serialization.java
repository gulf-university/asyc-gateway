package com.gulf.async.gateway.common.serialize;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by xubai on 2018/11/07 5:30 PM.
  */
public interface Serialization {
    /**
     * get content type id
     *
     * @return content type id
     */
    byte getContentTypeId();

    /**
     * get content type
     *
     * @return content type
     */
    String getContentType();

    /**
     * create serializer
     * @param output
     * @return serializer
     * @throws IOException
     */
    ObjectOutput serialize(OutputStream output) throws IOException;

    /**
     * create deserializer
     * @param input
     * @return deserializer
     * @throws IOException
     */
    ObjectInput deserialize(InputStream input) throws IOException;
}
