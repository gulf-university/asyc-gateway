package com.gulf.async.gateway.common.spi;

import java.lang.annotation.*;

/**
 * Created by xubai on 2019/07/29 11:44 AM.
 *
 * 自定义spi机制
 * refer: http://dubbo.apache.org/zh-cn/blog/introduction-to-dubbo-spi.html
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface SPI {

    String value() default "";

}
