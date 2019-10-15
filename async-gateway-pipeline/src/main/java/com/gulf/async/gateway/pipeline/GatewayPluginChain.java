package com.gulf.async.gateway.pipeline;

import com.gulf.async.gateway.common.spi.SpiLoader;
import com.gulf.async.gateway.common.util.CollectionUtil;
import com.gulf.async.gateway.remoting.api.context.RemotingContext;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by xubai on 2019/10/10 8:50 PM.
 *
 * refer: {@link io.netty.channel.ChannelPipeline}
 *
 */
public class GatewayPluginChain {

    private static List<GatewayPlugin> prePlugins;

    private static List<GatewayPlugin> postPlugins;


    static{
        prePlugins = SpiLoader.getSpiLoader(GatewayPlugin.class).getAdativateSpi(GatewayPlugin.PLUGIN_GROUP);
        if (CollectionUtil.isNotEmpty(prePlugins)){
            postPlugins = prePlugins;
            Collections.sort(postPlugins, GatewayPlugin.DESC_COMPARATOR);
        }
    }

    public static RemotingContext preInvoke(RemotingContext context){
        if (CollectionUtil.isNotEmpty(prePlugins)){
            for (GatewayPlugin plugin : prePlugins){
                try {
                    plugin.pre(context);
                } catch (Throwable e) {

                }
            }
        }
        return context;
    }

    public static RemotingContext postInvoke(RemotingContext context){
        if (CollectionUtil.isNotEmpty(postPlugins)){
            for (GatewayPlugin plugin : postPlugins){
                plugin.post(context);
            }
        }
        return context;
    }

}
