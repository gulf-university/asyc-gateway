package com.gulf.async.gateway.plugin;

import com.async.gateway.register.spi.RegistryListener;
import com.gulf.async.gateway.common.log.Logger;
import com.gulf.async.gateway.common.log.LoggerFactory;
import com.gulf.async.gateway.pipeline.AbstractGatewayPlugin;
import com.gulf.async.gateway.spi.remote.RemotingContext;
import com.gulf.async.gateway.spi.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * Created by xubai on 2019/12/03 11:06 AM.
 */
public class ServiceRequestLimiter extends AbstractGatewayPlugin implements RegistryListener {

    private final static Logger LOG = LoggerFactory.getInstance(ServiceRequestLimiter.class);

    private final static int DEFAULT_SERVICE_CONCURRENCY = 300;
    private final static int DEFAULT_SHARED_CONCURRENCY = 10000;
    private final static Semaphore SHARED_SEMAPHORE = new Semaphore(DEFAULT_SHARED_CONCURRENCY);

    public final Map<String/*addr*/, Integer> serviceCurrency = new ConcurrentHashMap<>();
    public final Map<String/*addr*/, Semaphore> serviceSemaphores = new ConcurrentHashMap<>();


    @Override
    public void pre(RemotingContext context) {
        int concurrency = serviceCurrency.getOrDefault(context.request().id(), DEFAULT_SERVICE_CONCURRENCY);
        Semaphore sp = serviceSemaphores.getOrDefault(context.request().id(), SHARED_SEMAPHORE);
        boolean accquired = false;
        try {
            accquired = sp.tryAcquire(3000L, TimeUnit.MILLISECONDS);
            super.pre(context);
        }catch (Exception e){
            LOG.warn("service:{} acquire semaphore fail, current concurrency:{}", context.request().id(), concurrency - sp.availablePermits());
        }finally {
            if (accquired){
                sp.release();
            }
        }
    }

    @Override
    public void online(Service s) {
        //TODO 应用或服务级别并发设置
        serviceCurrency.put(s.name(), DEFAULT_SERVICE_CONCURRENCY);
        serviceSemaphores.put(s.name(), new Semaphore(DEFAULT_SERVICE_CONCURRENCY));
    }

    @Override
    public void offline(Service s) {
        serviceSemaphores.remove(s.name());
    }
}
