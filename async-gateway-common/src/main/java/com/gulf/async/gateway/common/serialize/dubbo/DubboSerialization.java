/*
 * Copyright 1999-2011 Alibaba Group.
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
package com.gulf.async.gateway.common.serialize.dubbo;

import com.gulf.async.gateway.common.serialize.ObjectInput;
import com.gulf.async.gateway.common.serialize.ObjectOutput;
import com.gulf.async.gateway.common.serialize.Serialization;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author ding.lid
 */
public class DubboSerialization implements Serialization {

    public byte getContentTypeId() {
        return 1;
    }

    public String getContentType() {
        return "x-application/dubbo";
    }

    public ObjectOutput serialize(OutputStream out) throws IOException {
        return new GenericObjectOutput(out);
    }

    public ObjectInput deserialize(InputStream is) throws IOException {
        return new GenericObjectInput(is);
    }

}