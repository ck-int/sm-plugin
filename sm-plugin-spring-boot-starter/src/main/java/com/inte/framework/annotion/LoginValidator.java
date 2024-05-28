package com.inte.framework.annotion;

import java.lang.annotation.*;

/**
 * 登录参数校验
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LoginValidator {
    boolean validated() default true;

}
