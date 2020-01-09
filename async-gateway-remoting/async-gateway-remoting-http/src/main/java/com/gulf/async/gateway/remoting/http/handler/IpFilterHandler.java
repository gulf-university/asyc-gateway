package com.gulf.async.gateway.remoting.http.handler;

import com.gulf.async.gateway.common.log.Logger;
import com.gulf.async.gateway.common.log.LoggerFactory;
import com.gulf.async.gateway.common.spi.SpiLoader;
import com.gulf.async.gateway.store.spi.BlackListStore;
import com.gulf.async.gateway.store.spi.entity.BlackListEntity;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.ipfilter.AbstractRemoteAddressFilter;
import io.netty.handler.ipfilter.IpFilterRule;
import io.netty.handler.ipfilter.IpFilterRuleType;
import io.netty.handler.ipfilter.IpSubnetFilterRule;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by xubai on 2019/09/25 3:45 PM.
 */
@ChannelHandler.Sharable
public class IpFilterHandler extends AbstractRemoteAddressFilter<InetSocketAddress> implements BlackListFilter {

    private final static Logger LOG = LoggerFactory.getInstance(IpFilterHandler.class);

    private final static int DEDAULT_INTERNAL_NETMASK = 24;


    private Map<String, IpFilterRule> blackList = new ConcurrentHashMap<>();

    private BlackListStore blackListStore;

    public IpFilterHandler() {
        blackListStore = (BlackListStore)SpiLoader.getSpiLoader(BlackListStore.class).getAdativateSpi("blackListStore", "redis").get(0);
    }

    @Override
    protected boolean accept(ChannelHandlerContext ctx, InetSocketAddress remoteAddress) throws Exception {
        for (IpFilterRule rule : blackList.values()) {
            if (rule.matches(remoteAddress) && rule.ruleType() == IpFilterRuleType.REJECT) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void addIp(String ip) {
        blackList.putIfAbsent(ip, new IpSubnetFilterRule(ip, DEDAULT_INTERNAL_NETMASK, IpFilterRuleType.REJECT));
        blackListStore.save(new BlackListEntity()
                .setIp(ip)
                .setNetmask(DEDAULT_INTERNAL_NETMASK));
        LOG.info("blackFilter add black IP:{}", ip);
    }

    @Override
    public void addIpSubnet(String ip, Integer netmask) {
        blackList.putIfAbsent(ip, new IpSubnetFilterRule(ip, netmask, IpFilterRuleType.REJECT));
        blackListStore.save(new BlackListEntity()
                .setIp(ip)
                .setNetmask(netmask));
        LOG.info("blackFilter add black IP:{} netmask:{}", ip, netmask);
    }

    @Override
    public void loadAllIp() {
        List<BlackListEntity> allBlackList = blackListStore.list();
        if (allBlackList != null){
            for (BlackListEntity entity : allBlackList){
                IpFilterRule rule = new IpSubnetFilterRule(entity.getIp(),
                        (entity != null && entity.getNetmask() > 0) ? entity.getNetmask() : DEDAULT_INTERNAL_NETMASK,
                        IpFilterRuleType.REJECT);
                blackList.putIfAbsent(entity.getIp(), rule);
                LOG.info("blackFilter load black IP:{} ", entity);
            }
        }
    }
}
