package com.gulf.async.gateway.remoting.dubbo.utils;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * @author xubai
 */
public class Invocation {
    private String serverName;
    private String serviceClassName;
    private String methodName;
    private Class<?>[] paramTypes;
    private Object[] paramVal;
    private Object result;
    private Class<?> returnType;
    private Type[] returnTypes;
    private String url;
    private int timeout=30000;
    private String invocationKey;
    private byte[]  header;
    private byte[]  body;
    private int code;
    private Map<String, String> attachments;
    private CommunicationMode communicationMode=CommunicationMode.ASYNC;

    /**
     * the invocation to cache the call method info,use for invoker
     * @param method the call method
     * @param args the call method value
     */
    public Invocation(Method method, Object[] args){
        serverName =method.getDeclaringClass().getSimpleName();
        serviceClassName=method.getDeclaringClass().getName();
        methodName=method.getName();
        paramTypes=method.getParameterTypes();
        paramVal =args;
        returnType=method.getReturnType();
        returnTypes=new Type[]{method.getReturnType(), method.getGenericReturnType()};
        invocationKey= generateServiveKey(serverName,methodName,paramTypes);
    }

    public Object[] getParamVal() {
        return paramVal;
    }

    public String getServerName() {
        return serverName;
    }

    public String getMethodName() {
        return methodName;
    }

    public Class<?>[] getParamTypes() {
        return paramTypes;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getInvocationKey() {
        return invocationKey;
    }

    public byte[] getHeader() {
        return header;
    }

    public void setHeader(byte[] header) {
        this.header = header;
    }

    public CommunicationMode getCommunicationMode() {
        return communicationMode;
    }

    public void setCommunicationMode(CommunicationMode communicationMode) {
        this.communicationMode = communicationMode;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public Map<String, String> getAttachments() {
        return attachments;
    }

    public void setAttachments(Map<String, String> attachments) {
        this.attachments = attachments;
    }

    public String getAttachment(String key){
        return this.attachments.get(key);
    }

    public Class<?> getReturnType() {
        return returnType;
    }

    public void setReturnType(Class<?> returnType) {
        this.returnType = returnType;
    }

    public Type[] getReturnTypes() {
        return returnTypes;
    }

    public void setReturnTypes(Type[] returnTypes) {
        this.returnTypes = returnTypes;
    }

    public String getServiceClassName() {
        return serviceClassName;
    }

    public void setServiceClassName(String serviceClassName) {
        this.serviceClassName = serviceClassName;
    }

    private String generateServiveKey(String serverName,String methodName,Class<?>[] paramTypes){
        StringBuilder sb=new StringBuilder();
        sb.append(serverName);
        sb.append("/");
        sb.append(methodName);
        if(paramTypes!=null){
            for(Class c:paramTypes){
                sb.append("/");
                sb.append(c.getSimpleName());
            }
        }
        return sb.toString();
    }
}
