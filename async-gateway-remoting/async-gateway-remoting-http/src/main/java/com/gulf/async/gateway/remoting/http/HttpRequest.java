package com.gulf.async.gateway.remoting.http;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by xubai on 2018/11/07 6:18 PM.
 *
 * /api/{domain}/{serviceName}/{methodName}
 */
public class HttpRequest {

    public final static String URL_PREFIX = "/api/";

    private String id;

    private String remoteAddr;
    private String remoteIP;

    private String path;

    private String domain;
    private String serviceName;
    private String methodName;

    private long timestamp;



    private Map<String,Object> headers = new HashMap<>();

    private byte[] body;

    public String getRemoteAddr() {
        return remoteAddr;
    }

    public void setRemoteAddr(String remoteAddr) {
        this.remoteAddr = remoteAddr;
    }

    public String getRemoteIP() {
        return remoteIP;
    }

    public void setRemoteIP(String remoteIP) {
        this.remoteIP = remoteIP;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }


    public Map<String, Object> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, Object> headers) {
        this.headers = headers;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "HttpRequest{" +
                "id='" + id + '\'' +
                ", remoteAddr='" + remoteAddr + '\'' +
                ", remoteIP='" + remoteIP + '\'' +
                ", path='" + path + '\'' +
                ", domain='" + domain + '\'' +
                ", serviceName='" + serviceName + '\'' +
                ", methodName='" + methodName + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
