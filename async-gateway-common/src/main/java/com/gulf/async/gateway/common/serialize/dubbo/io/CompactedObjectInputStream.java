package com.gulf.async.gateway.common.serialize.dubbo.io;

import com.gulf.async.gateway.common.util.ClassHelper;

import java.io.*;

/**
 * Created by xubai on 2018/11/07 5:30 PM.
  */
public class CompactedObjectInputStream extends ObjectInputStream {
    private ClassLoader mClassLoader;

    public CompactedObjectInputStream(InputStream in) throws IOException
    {
        this(in, Thread.currentThread().getContextClassLoader());
    }

    public CompactedObjectInputStream(InputStream in, ClassLoader cl) throws IOException
    {
        super(in);
        mClassLoader = cl == null ? ClassHelper.getClassLoader() : cl;
    }

    @Override
    protected ObjectStreamClass readClassDescriptor() throws IOException,ClassNotFoundException
    {
        int type = read();
        if( type < 0 )
            throw new EOFException();
        switch( type )
        {
            case 0:
                return super.readClassDescriptor();
            case 1:
                Class<?> clazz = loadClass(readUTF());
                return ObjectStreamClass.lookup(clazz);
            default:
                throw new StreamCorruptedException("Unexpected class descriptor type: " + type);
        }
    }

    private Class<?> loadClass(String className) throws ClassNotFoundException
    {
        return mClassLoader.loadClass(className);
    }
}
