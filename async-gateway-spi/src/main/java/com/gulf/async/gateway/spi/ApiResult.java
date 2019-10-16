package com.gulf.async.gateway.spi;

import java.io.Serializable;

/**
 * Created by xubai on 2019/10/16 12:15 PM.
 */
public class ApiResult implements Serializable {

    private int code;

    private Object data=null;

    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

}
