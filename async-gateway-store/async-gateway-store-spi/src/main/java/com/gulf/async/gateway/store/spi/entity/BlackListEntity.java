package com.gulf.async.gateway.store.spi.entity;

import lombok.Data;
import lombok.Getter;
import lombok.ToString;

/**
 * Created by xubai on 2019/09/26 3:14 PM.
 */
@Getter
@ToString
public class BlackListEntity {

    //ip地址规则
    private String ip;

    //掩码
    private Integer netmask;

    //状态: 0 无效；1 有效；
    private Integer status = 1;

    //备注
    private String remark;

    public BlackListEntity setIp(String ip) {
        this.ip = ip;
        return this;
    }

    public BlackListEntity setNetmask(Integer netmask) {
        this.netmask = netmask;
        return this;
    }

    public BlackListEntity setStatus(Integer status) {
        this.status = status;
        return this;
    }

    public BlackListEntity setRemark(String remark) {
        this.remark = remark;
        return this;
    }
}
