package com.inte.framework.autoconfigure;

/**
 * @author ck
 */
public interface PackageScanConfigurer {

    default void configure(PackageScanRegistry registry) {
    }
}
