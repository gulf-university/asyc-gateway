package com.gulf.async.gateway.remoting.dubbo;

import com.gulf.async.gateway.remoting.api.Protocol;
import com.gulf.async.gateway.remoting.api.context.RemotingContext;
import com.gulf.async.gateway.remoting.api.context.RemotingRequest;
import com.gulf.async.gateway.remoting.api.invoke.Invocation;
import com.gulf.async.gateway.remoting.dubbo.context.DubboRequest;
import com.gulf.async.gateway.remoting.dubbo.context.DubboResponse;
import com.gulf.async.gateway.remoting.dubbo.utils.DubboRequstCache;
import com.gulf.async.gateway.remoting.dubbo.utils.ExchangeCodec;
import com.gulf.async.gateway.remoting.dubbo.utils.Request;
import com.gulf.async.gateway.remoting.dubbo.utils.RpcInvocation;
import com.gulf.async.gateway.spi.Service;
import com.gulf.async.gateway.spi.ha.ServiceNode;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

import java.io.IOException;

/**
 * Created by xubai on 2019/10/16 3:50 PM.
 */
public class DubboInvocation implements Invocation {

    private Service service;

    private ServiceNode serviceNode;

    private Protocol command;

    private RemotingRequest source;

    public DubboInvocation(Service service, ServiceNode serviceNode, RemotingRequest request) {
        this.service = service;
        this.serviceNode = serviceNode;
        this.buildCommand();
        this.source = request;
    }

    private void buildCommand(){
        command = new DubboRequest();
        Request reqR = new Request();
        reqR.setTwoWay(true);
        DubboResponse responseCommand = DubboResponse.createResponseCommand((DubboRequest) command);
        responseCommand.setUrl(serviceNode.url());
        DubboRequstCache.addRpcRequst(reqR.getId(), (DubboRequest)command);
        try {
            requestChangeDubboRequest((DubboRequest)command, reqR);
        } catch (Exception e) {
            DubboRequstCache.removeRpcRequst(reqR.getId());
        }
    }


    @Override
    public Service service() {
        return service;
    }

    @Override
    public ServiceNode serviceNode() {
        return serviceNode;
    }

    @Override
    public Protocol command() {
        return command;
    }

    private Protocol requestChangeDubboRequest(DubboRequest request, Request reqR) throws IOException, ClassNotFoundException {
        DubboRequest requestCommandRpc = request;
        RpcInvocation rpcInvocation = new RpcInvocation();
        DubboService dubboService = (DubboService) service;
        rpcInvocation.setServiceClassName(dubboService.getInterfaceName());
        rpcInvocation.setMethodName(dubboService.getMethodName());
        rpcInvocation.setParameterTypes(new Class[]{byte[].class});
        rpcInvocation.setArguments(new Object[]{source.getBody()});
        rpcInvocation.setRequestData(source.getBody());
        rpcInvocation.setAttachment("version", dubboService.getVersion());

        reqR.setData(rpcInvocation);

        ByteBuf byteBuf = ByteBufAllocator.DEFAULT.buffer();
        ExchangeCodec.encodeRequest(byteBuf, reqR);

        requestCommandRpc.setBodyBuf(byteBuf);
        String url = serviceNode.url();
        //requestCommandRpc.setUrl(url);
        //requestCommandRpc.setServiceId(request.getServiceId());
        requestCommandRpc.setRequestId(reqR.getId());
        requestCommandRpc.setHttpRequestId(source.id());

        return requestCommandRpc;
    }
}
