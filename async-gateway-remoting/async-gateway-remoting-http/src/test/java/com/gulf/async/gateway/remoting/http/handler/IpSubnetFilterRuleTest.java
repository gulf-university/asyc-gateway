package com.gulf.async.gateway.remoting.http.handler;

import io.netty.handler.ipfilter.IpFilterRule;
import io.netty.handler.ipfilter.IpFilterRuleType;
import io.netty.handler.ipfilter.IpSubnetFilterRule;
import org.junit.Test;

import java.net.InetSocketAddress;

import static org.junit.Assert.assertTrue;

/**
 * Created by xubai on 2019/09/25 8:08 PM.
 */
public class IpSubnetFilterRuleTest {

    @Test
    public void whiteList()throws Exception{
        IpFilterRule whiteIp = new IpSubnetFilterRule("10.37.100.100", 24, IpFilterRuleType.ACCEPT);
        assertTrue("white ip no match:"+whiteIp.ruleType(), whiteIp.matches(new InetSocketAddress("10.37.100.101", 1234)));
    }

    @Test
    public void blackList()throws Exception{
        IpFilterRule whiteIp = new IpSubnetFilterRule("10.37.100.100", 24, IpFilterRuleType.REJECT);
        assertTrue("black ip no match:"+whiteIp.ruleType(), whiteIp.matches(new InetSocketAddress("10.37.10.100", 1234)));
    }

}
