package com.admin.common.annotation;

import com.admin.common.enums.Logical;

import java.lang.annotation.*;

/**
 * 权限校验注解
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequiresPermissions {
    /**
     * 权限标识
     */
    String[] value();

    /**
     * 权限组合方式
     */
    Logical logical() default Logical.AND;
}
