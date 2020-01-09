package com.gulf.async.gateway.store.redis;

import com.gulf.async.gateway.common.spi.Activate;
import com.gulf.async.gateway.store.spi.BlackListStore;
import com.gulf.async.gateway.store.spi.entity.BlackListEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xubai on 2020/01/08 7:53 PM.
 */
@Activate(group = {"BlackListStore"}, tag = {"redis"})
public class BlackListStoreImpl implements BlackListStore {

    @Override
    public void save(BlackListEntity entity) {
        //TODO
    }

    @Override
    public List<BlackListEntity> list() {
        //TODO
        return new ArrayList<>();
    }
}
