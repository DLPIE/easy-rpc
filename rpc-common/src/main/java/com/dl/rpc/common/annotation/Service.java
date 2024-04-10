package com.dl.rpc.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 代表一个服务提供类
 */
@Target(ElementType.TYPE) // 修饰类
@Retention(RetentionPolicy.RUNTIME)
public @interface Service {
    public String name() default "";
}
