package com.gulf.async.gateway.remoting.dubbo.context;

import com.gulf.async.gateway.remoting.api.context.RemotingRequest;

/**
 * Created by xubai on 2019/10/11 6:05 PM.
 */
public class DubboRequest extends DubboProtocol {

    public static DubboRequest createRequestCommand(int code) {
        DubboRequest cmd = new DubboRequest();
        //cmd.setCode(code);
        //setCmdVersion(cmd);
        return cmd;
    }

}
