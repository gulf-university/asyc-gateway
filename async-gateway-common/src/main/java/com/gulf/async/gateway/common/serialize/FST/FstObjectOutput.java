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


import com.gulf.async.gateway.common.serialize.ObjectOutput;
import org.nustaq.serialization.FSTObjectOutput;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


public class FstObjectOutput implements ObjectOutput {

    private FSTObjectOutput output;
    private ByteArrayOutputStream os;
    public FstObjectOutput() {
        this.os = new ByteArrayOutputStream();

        output = FstFactory.getDefaultFactory().getObjectOutput(os);
    }

    public void writeBool(boolean v) throws IOException {
        output.writeBoolean(v);
    }

    public void writeByte(byte v) throws IOException {
        output.writeByte(v);
    }

    public void writeShort(short v) throws IOException {
        output.writeShort(v);
    }

    public void writeInt(int v) throws IOException {
        output.writeInt(v);
    }

    public void writeLong(long v) throws IOException {
        output.writeLong(v);
    }

    public void writeFloat(float v) throws IOException {
        output.writeFloat(v);
    }

    public void writeDouble(double v) throws IOException {
        output.writeDouble(v);
    }

    public void writeBytes(byte[] v) throws IOException {
        if (v == null) {
            output.writeInt(-1);
        } else {
            writeBytes(v, 0, v.length);
        }
    }

    public void writeBytes(byte[] v, int off, int len) throws IOException {
        if (v == null) {
            output.writeInt(-1);
        } else {
            output.writeInt(len);
            output.write(v, off, len);
        }
    }


    public void writeUTF(String v) throws IOException {
        output.writeUTF(v);
    }

    public void writeObject(Object v) throws IOException {
        output.writeObject(v);
    }

    public void flushBuffer() throws IOException {
        output.flush();
    }


    public byte[] getBytes() throws IOException {
        output.flush();
        return this.os.toByteArray();
    }
    public void reset(){
        os.reset();
    }

    public void clear(){
        try {
            this.os.close();
            //todo:异常处理
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}