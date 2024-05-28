package com.inte.framework.annotion;

import java.lang.annotation.*;

/**
 * 解密注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DecryptionAnnotation {
}
