package com.gulf.async.gateway.common.serialize;

import java.io.IOException;

/**
 * @author xubai
 */
public interface ObjectOutput extends DataOutput {
    /**
     * write object.
     *
     * @param obj object.
     */
    void writeObject(Object obj) throws IOException;
    byte[] getBytes() throws IOException;
    void reset();
    void clear();
}
