package com.gulf.async.gateway.remoting.http.handler;

/**
 * Created by xubai on 2019/09/26 2:37 PM.
 */
public interface BlackListFilter {

    /**
     *
     * @param ip 黑名单Ip，支持正则
     */
    void addIp(String ip);

    /**
     * @param ip 黑名单Ip，支持正则
     * @param netmask 子网掩码
     */
    void addIpSubnet(String ip, Integer netmask);

    void loadAllIp();

}
