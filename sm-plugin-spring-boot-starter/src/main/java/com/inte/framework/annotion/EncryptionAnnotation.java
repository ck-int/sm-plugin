package com.inte.framework.annotion;

import java.lang.annotation.*;

/**
 * 加密
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EncryptionAnnotation {

}