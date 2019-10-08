package com.gulf.async.gateway.common.util;

/**
 * Created by xubai on 2019/09/26 11:02 AM.
 */
public class NetworkUtil {

    public static boolean isIpv6(String ip){
        return ip.indexOf(":") > 0;
    }

}
