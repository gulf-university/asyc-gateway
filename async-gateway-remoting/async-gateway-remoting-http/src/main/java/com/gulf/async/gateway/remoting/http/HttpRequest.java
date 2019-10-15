package com.gulf.async.gateway.remoting.http;

import lombok.Data;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by xubai on 2018/11/07 6:18 PM.
 *
 * /api/{domain}/{serviceName}/{methodName}
 */
@Data
@ToString
public class HttpRequest {

    private String domain;
    private String service;
    private String method;
    private String version = "1.0.0";

    private Object body;

    private String requestId;

    private Map<String, String> headers = new HashMap<>();


}
