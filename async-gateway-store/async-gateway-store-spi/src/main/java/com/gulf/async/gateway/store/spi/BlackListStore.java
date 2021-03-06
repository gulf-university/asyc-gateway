package com.gulf.async.gateway.store.spi;

import com.gulf.async.gateway.common.spi.SPI;
import com.gulf.async.gateway.store.spi.entity.BlackListEntity;

import java.util.List;

/**
 * Created by xubai on 2019/09/26 3:14 PM.
 */
@SPI
public interface BlackListStore {

    void save(BlackListEntity entity);

    List<BlackListEntity> list();

}
