/**
 * Copyright 1999-2014 dangdang.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.gulf.async.gateway.common.serialize.FST;

import com.gulf.async.gateway.common.serialize.ObjectInput;
import org.nustaq.serialization.FSTObjectInput;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Type;


public class FstObjectInput implements ObjectInput {

    private FSTObjectInput input;
    private ByteArrayInputStream mIs;

    public FstObjectInput(byte[] bytes ) {
        this.mIs =new ByteArrayInputStream(bytes);
        input = FstFactory.getDefaultFactory().getObjectInput(mIs);
    }

    public boolean readBool() throws IOException {
            return input.readBoolean();
    }

    public byte readByte() throws IOException {
            return input.readByte();
    }

    public short readShort() throws IOException {
            return input.readShort();
    }

    public int readInt() throws IOException {
            return input.readInt();
    }

    public long readLong() throws IOException {
            return input.readLong();
    }

    public float readFloat() throws IOException {
            return input.readFloat();
    }

    public double readDouble() throws IOException {
            return input.readDouble();
    }

    public byte[] readBytes() throws IOException {
            int len = input.readInt();
            if (len < 0) {
                return null;
            } else if (len == 0) {
                return new byte[]{};
            } else {
                byte[] b = new byte[len];
                input.readFully(b);
                return b;
            }
    }

    public String readUTF() throws IOException {
            return input.readUTF();
    }

    public Object readObject() throws IOException, ClassNotFoundException {
            return input.readObject();
    }

    @SuppressWarnings("unchecked")
    public <T> T readObject(Class<T> clazz) throws IOException, ClassNotFoundException {
        try {
            return (T)input.readObject(clazz);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public <T> T readObject(Class<T> clazz, Type type) throws IOException, ClassNotFoundException {
        return  readObject(clazz);
    }

    public void clear(){
        try {
            this.mIs.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}