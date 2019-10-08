package com.gulf.async.gateway.remoting.http.plugins.blacklist;

/**
 * Created by xubai on 2019/09/26 2:37 PM.
 */
public interface BlackListFilter {

    void addIp(String ip);

    /**
     * @param ip
     * @param netmask 子网掩码
     */
    void addIpSubnet(String ip, Integer netmask);

    void loadAllIp();

}
