package com.async.gateway.register.api;

import com.gulf.async.gateway.spi.Service;

/**
 * Created by xubai on 2019/12/03 11:14 AM.
 */
public interface RegistryListener {

    void online(Service s);

    void offline(Service s);

}
