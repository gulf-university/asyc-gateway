package com.gulf.async.gateway.remoting.dubbo.utils;

/**
 * Created by xubai on 2018/11/07 5:30 PM.
  */
public class ResponseCode {
    // 成功
    public static final int SUCCESS = 1;
    // 发生了未捕获异常
    public static final int SYSTEM_ERROR = 2;
    // 由于线程池拥堵，系统繁忙
    public static final int SYSTEM_BUSY = 3;
    // 请求代码不支持
    public static final int REQUEST_CODE_NOT_SUPPORTED = 4;
}
