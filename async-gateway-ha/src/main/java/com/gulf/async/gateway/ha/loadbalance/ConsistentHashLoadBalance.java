package com.gulf.async.gateway.ha.loadbalance;

import com.gulf.async.gateway.common.log.Logger;
import com.gulf.async.gateway.common.log.LoggerFactory;
import com.gulf.async.gateway.spi.ha.ServiceNode;
import com.gulf.async.gateway.spi.ha.ServiceNodes;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by xubai on 2018/11/09 11:40 AM.
 */
public class ConsistentHashLoadBalance extends AbstractLoadBalance{

    private final static Logger LOG = LoggerFactory.getInstance(ConsistentHashLoadBalance.class);

    private final Map<String, ConsistentHashSelector> hashSelectorMap = new ConcurrentHashMap();


    @Override
    protected String doSelect(ServiceNodes nodes, Map<String, Object> options) {
        if (options == null || options.size() == 0){
            return nodes.getRandomNode().url();
        }
        String serviceName = nodes.getService().name();
        int nodesHash = System.identityHashCode(nodes.getNodeList());
        ConsistentHashSelector selector = hashSelectorMap.get(serviceName);
        if (selector == null || selector.getNodesHash() != nodesHash){
            hashSelectorMap.put(serviceName, new ConsistentHashSelector(nodes));
            selector = hashSelectorMap.get(serviceName);
        }
        String url = selector.select((byte[])options.get("bytes"));
        return url;
    }



    class ConsistentHashSelector{

        private final TreeMap<Long, ServiceNode> virtualNodeMap = new TreeMap<>();
        private final int virtualNodeSize = 100;
        private final int hashCount = 4;

        private int nodesHash;

        public ConsistentHashSelector(ServiceNodes nodes) {
             nodesHash = System.identityHashCode(nodes.getNodeList());
             for (ServiceNode node : nodes.getNodeList()){
                 for (int i = 0; i < virtualNodeSize/hashCount; i++) {
                     try {
                         byte[] md5Bytes = this.md5((node.url()+i).getBytes("UTF-8"));
                         for (int j = 0; j < hashCount; j++) {
                             long hash = hash(md5Bytes, j);
                             virtualNodeMap.put(hash, node);
                         }
                     } catch (UnsupportedEncodingException e) {
                         LOG.error("node.getBytes error:", e.getMessage());
                     }

                 }
             }
        }

        public int getNodesHash(){
            return nodesHash;
        }

        public String select(byte[] bytes){
            long hash = hash(md5(bytes), 0);
            if (!virtualNodeMap.containsKey(hash)){
                SortedMap<Long, ServiceNode> tailMap = virtualNodeMap.tailMap(hash);
                hash = (tailMap != null) ? tailMap.firstKey() : virtualNodeMap.firstKey();
            }
            return virtualNodeMap.get(hash).url();
        }

        private byte[] md5(byte[] byteData) {
            MessageDigest md5 = null;
            try {
                md5 = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
                LOG.error("md5 instance exception", e);
            }
            md5.reset();
            md5.update(byteData);
            return md5.digest();
        }

        private long hash(byte[] md5Data, int number) {
            return (((md5Data[3 + number * 4] & 0xFF) << 24)
                    | ((md5Data[2 + number * 4] & 0xFF) << 16)
                    | ((md5Data[1 + number * 4] & 0xFF) << 8)
                    | (md5Data[0 + number * 4] & 0xFF))
                    & 0xFFFFFFFFL;
        }
    }
}
