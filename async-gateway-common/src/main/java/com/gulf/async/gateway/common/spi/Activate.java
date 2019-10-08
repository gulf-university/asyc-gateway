package com.gulf.async.gateway.common.spi;

import java.lang.annotation.*;

/**
 * Created by xubai on 2019/07/29 1:57 PM.
 *
 * spi扩展类
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Activate {

    //分组
    String[] group();

    //排序
    int sort() default 0;

    //标签
    String[] tag() default {};


}
