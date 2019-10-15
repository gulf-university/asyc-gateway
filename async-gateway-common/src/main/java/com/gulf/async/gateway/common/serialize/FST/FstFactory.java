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


import org.nustaq.serialization.FSTConfiguration;
import org.nustaq.serialization.FSTObjectInput;
import org.nustaq.serialization.FSTObjectOutput;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class FstFactory {

    private static final FstFactory factory = new FstFactory();

    private final FSTConfiguration conf = FSTConfiguration.createDefaultConfiguration();


    public static FstFactory getDefaultFactory() {
        return factory;
    }

    public FstFactory() {
        conf.registerClass(HashMap.class);
        conf.registerClass(ArrayList.class);
        conf.registerClass(LinkedList.class);
        conf.registerClass(HashSet.class);
        conf.registerClass(TreeSet.class);
        conf.registerClass(Hashtable.class);
        conf.registerClass(Date.class);
        conf.registerClass(Calendar.class);
        conf.registerClass(ConcurrentHashMap.class);
        conf.registerClass(GregorianCalendar.class);
        conf.registerClass(Vector.class);
        conf.registerClass(BitSet.class);

        for (Class clazz : SerializableClassRegistry.getRegisteredClasses()) {
            conf.registerClass(clazz);
        }
    }

    public FSTObjectOutput getObjectOutput(OutputStream outputStream) {
        return conf.getObjectOutput(outputStream);
    }

    public FSTObjectInput getObjectInput(InputStream inputStream) {
        return conf.getObjectInput(inputStream);
    }
}
