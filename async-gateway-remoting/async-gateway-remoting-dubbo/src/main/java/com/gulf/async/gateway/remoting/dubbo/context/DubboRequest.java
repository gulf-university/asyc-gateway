package com.gulf.async.gateway.remoting.dubbo.context;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by xubai on 2019/10/11 6:05 PM.
 */
@Getter
@Setter
public class DubboRequest extends DubboProtocol {

    private String httpRequestId;

    public static DubboRequest createRequestCommand(int code) {
        DubboRequest cmd = new DubboRequest();
        //cmd.setCode(code);
        //setCmdVersion(cmd);
        return cmd;
    }

}
