package com.gulf.async.gateway.remoting.dubbo.utils;

import java.io.Serializable;

/**
 * Created by xubai on 2018/11/07 5:30 PM.
  */
public class Result implements Serializable {

    private static final long serialVersionUID = 7805599364946761863L;

    private Throwable throwable=null;

    //1执行成功，2执行失败，3系统未知错误
    private int state=ResponseCode.SUCCESS;

    private Object result=null;

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
