package com.example.codesandbox.security.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)     // 用在方法上
@Retention(RetentionPolicy.RUNTIME)  // 运行时保留
@Documented
public @interface RequirePermission {

    // TODO: 定义resource属性（String类型）
    String resource();

    // TODO: 定义action属性（String类型）
    String action();
}
