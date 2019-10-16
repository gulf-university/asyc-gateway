package com.gulf.async.gateway.remoting.dubbo;

import com.gulf.async.gateway.spi.Service;

/**
 * Created by xubai on 2019/10/16 4:35 PM.
 */
public class DubboService implements Service {

    public final static String NAME_PATTERN = "{interface}.{method}.{version}";

    private String interfaceName;
    private String methodName;
    private String version = "1.0.0";


    public DubboService() {
    }

    public DubboService(String interfaceName, String methodName) {
        this.interfaceName = interfaceName;
        this.methodName = methodName;
    }

    public DubboService(String interfaceName, String methodName, String version) {
        this.interfaceName = interfaceName;
        this.methodName = methodName;
        this.version = version;
    }

    @Override
    public String name() {
        return null;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public DubboService setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
        return this;
    }

    public String getMethodName() {
        return methodName;
    }

    public DubboService setMethodName(String methodName) {
        this.methodName = methodName;
        return this;
    }

    public String getVersion() {
        return version;
    }

    public DubboService setVersion(String version) {
        this.version = version;
        return this;
    }
}
