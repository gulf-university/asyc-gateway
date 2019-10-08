package com.gulf.async.gateway.common.config;

import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

/**
 * Created by xubai on 2019/09/24 8:58 PM.
 */
public class DefaultConfigs implements Configs {

    private final static AtomicReferenceFieldUpdater<DefaultConfigs, AtomicReferenceArray> ATTRIBUTES_UPDATER
            = AtomicReferenceFieldUpdater.newUpdater(DefaultConfigs.class, AtomicReferenceArray.class, "attributes");
    private static final int BUCKET_SIZE = 4;
    private static final int MASK = BUCKET_SIZE  - 1;

    private volatile AtomicReferenceArray<DefaultConfigValue<?>> attributes;

    private Object[] locks = new Object[BUCKET_SIZE];
    {
        {
            for (int i = 0; i < locks.length; i++) {
                locks[i] = new Object();
            }
        }
    };

    @Override
    public <T> ConfigValue<T> config(ConfigOption<T> key) {
        if (key == null){
            throw new NullPointerException("key");
        }
        AtomicReferenceArray<DefaultConfigValue<?>> exist = this.attributes;
        if (exist == null){
            exist = new AtomicReferenceArray<>(BUCKET_SIZE);
            if (!ATTRIBUTES_UPDATER.compareAndSet(this, null, exist)){
                this.attributes = exist;
            }
        }
        int i = index(key);
        DefaultConfigValue<?> head = exist.get(i);
        if (head == null){
            head = new DefaultConfigValue<T>();
            DefaultConfigValue<T> attr = new DefaultConfigValue<T>(key, head);
            head.next = attr;
            attr.prev = head;
            //
            if (exist.compareAndSet(i, null, head)){
                return attr;
            }else{
                head = exist.get(i);
            }
        }
        synchronized (locks[i]){
            DefaultConfigValue<?> curr = head;
            for (;;){
                DefaultConfigValue<?> next = curr.next;
                if (next == null){
                    DefaultConfigValue<T> attr = new DefaultConfigValue<T>(key, head);
                    curr.next = attr;
                    attr.prev = curr;
                    return attr;
                }
                if (next.key == key && !next.removed) {
                    return (ConfigValue<T>) next;
                }
                curr = next;
            }
        }
    }

    @Override
    public <T1> boolean contains(ConfigOption<T1> key) {
        if (key == null) {
            throw new NullPointerException("key");
        }
        AtomicReferenceArray<DefaultConfigValue<?>> exist = this.attributes;
        if (exist == null) {
            // no attribute exists
            return false;
        }

        int i = index(key);
        DefaultConfigValue<?> head = exist.get(i);
        if (head == null) {
            // No attribute exists which point to the bucket in which the head should be located
            return false;
        }

        // We need to synchronize on the head.
        synchronized (head) {
            // Start with head.next as the head itself does not store an attribute.
            DefaultConfigValue<?> curr = head.next;
            while (curr != null) {
                if (curr.key == key && !curr.removed) {
                    return true;
                }
                curr = curr.next;
            }
            return false;
        }
    }

    private static int index(ConfigOption<?> key) {
        return key.id() & MASK;
    }


    //默认属性类
    class DefaultConfigValue<T> extends AtomicReference<T> implements ConfigValue<T>{

        private ConfigOption<T> key;

        private DefaultConfigValue<?> head;
        private DefaultConfigValue<?> prev;
        private DefaultConfigValue<?> next;

        private volatile boolean removed;

        public DefaultConfigValue() {
            head = this;
            key = null;
        }

        public DefaultConfigValue(ConfigOption<T> key, DefaultConfigValue<?> head) {
            this.key = key;
            this.head = head;
        }

        @Override
        public ConfigOption<T> key() {
            return key;
        }


        @Override
        public T setIfAbsent(T value) {
            while (!compareAndSet(null, value)){
                T old = get();
                if (old != null){
                    return old;
                }
            }
            return null;
        }

        @Override
        public T getAndRemove() {
            removed = true;
            T oldValue = getAndSet(null);
            remove0();
            return oldValue;
        }

        //remove current node
        private void remove0(){
            synchronized (head){
                if (prev == null){
                    return;
                }
                prev.next = next;
                if (next != null){
                    next.prev = prev;
                }
                prev = null;
                next = null;
            }
        }
    }
    
}
