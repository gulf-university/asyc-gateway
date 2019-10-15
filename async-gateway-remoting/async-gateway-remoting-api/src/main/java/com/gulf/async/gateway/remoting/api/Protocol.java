package com.gulf.async.gateway.remoting.api;

import lombok.Data;

/**
 * Created by xubai on 2019/10/10 5:27 PM.
 */
@Data
public abstract  class Protocol {

    protected transient byte[] header;

    protected transient byte[] body;

}
