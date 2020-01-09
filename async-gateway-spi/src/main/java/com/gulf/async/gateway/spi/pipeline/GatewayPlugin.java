package com.gulf.async.gateway.spi.pipeline;

import com.gulf.async.gateway.common.spi.Activate;
import com.gulf.async.gateway.common.spi.SPI;
import com.gulf.async.gateway.spi.remote.RemotingContext;

import java.util.Comparator;

/**
 * Created by xubai on 2019/09/26 11:10 AM.
 */
@SPI
public interface GatewayPlugin {

    void pre(RemotingContext context);

    void post(RemotingContext context);

    String PLUGIN_GROUP = "plugin";

    Comparator<GatewayPlugin> DESC_COMPARATOR = new Comparator<GatewayPlugin>() {
        @Override
        public int compare(GatewayPlugin o1, GatewayPlugin o2) {
            Activate a1 = o1.getClass().getAnnotation(Activate.class);
            Activate a2 = o2.getClass().getAnnotation(Activate.class);
            if (a1 == null || a2 == null){
                return 0;
            }
            if (a1.sort() > a2.sort()){
                return -1;
            }else if (a1.sort() < a2.sort()){
                return 1;
            }
            return 0;
        }
    };

}
