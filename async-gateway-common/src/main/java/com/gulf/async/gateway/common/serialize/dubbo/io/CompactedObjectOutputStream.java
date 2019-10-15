package com.gulf.async.gateway.common.serialize.dubbo.io;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.OutputStream;

/**
 * Created by xubai on 2018/11/07 5:30 PM.
  */
public class CompactedObjectOutputStream extends ObjectOutputStream {
    public CompactedObjectOutputStream(OutputStream out) throws IOException
    {
        super(out);
    }

    @Override
    protected void writeClassDescriptor(ObjectStreamClass desc) throws IOException
    {
        Class<?> clazz = desc.forClass();
        if( clazz.isPrimitive() || clazz.isArray() )
        {
            write(0);
            super.writeClassDescriptor(desc);
        }
        else
        {
            write(1);
            writeUTF(desc.getName());
        }
    }
}
