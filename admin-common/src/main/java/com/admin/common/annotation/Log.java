package com.admin.common.annotation;

import com.admin.common.enums.BusinessType;

import java.lang.annotation.*;

/**
 * 操作日志注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Log {
    /**
     * 模块名称
     */
    String title();

    /**
     * 业务类型
     */
    BusinessType businessType() default BusinessType.OTHER;

    /**
     * 是否保存请求参数
     */
    boolean saveParam() default true;

    /**
     * 是否保存返回参数
     */
    boolean saveResult() default false;
}
