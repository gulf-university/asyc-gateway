package com.gulf.async.gateway.spi.ha;

/**
 * Created by xubai on 2019/10/11 12:31 PM.
 */
public interface ServiceNode {

    String url();

    Integer weight();

}
