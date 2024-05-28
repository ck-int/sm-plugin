package com.inte.framework.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * @author ck
 */
public final class ApplicationContextUtils {

    private static ApplicationContext context;

    private static boolean initialized = false;

    private ApplicationContextUtils() {
    }

   public static void initialize(ApplicationContext context) {
        Assert.isTrue(!initialized, "ApplicationContextUtils已经初始化完毕，不能再进行初始化");
        ApplicationContextUtils.context = context;
        initialized = true;
    }

    /**
     * 获取{@link ApplicationContext}对象，不会为null
     *
     * @return ApplicationContext
     */
    public static ApplicationContext getContext() {
        Assert.isTrue(initialized, "容器还没有被启动或者不是Spring Boot环境");
        return context;
    }

    /**
     * 根据bean的名称获取bean实例，不会返回{@code null}
     *
     * @param name bean的名称
     * @return bean实例，不为null
     * @throws BeansException 如果该bean不存在
     */
    public static Object getRequiredBean(String name) throws BeansException {
        return getContext().getBean(name);
    }

    /**
     * 根据bean的名称获取bean实例
     *
     * @param name bean的名称
     * @return bean实例，该bean不存在时返回null
     */
    @Nullable
    public static Object getBean(String name) {
        try {
            return getRequiredBean(name);
        } catch (BeansException e) {
            return null;
        }
    }

    /**
     * 根据bean的类型获取bean实例，不会返回{@code null}
     *
     * @param beanType bean的类型
     * @return bean实例，不为null
     * @throws BeansException 如果该bean不存在
     */
    public static <T> T getRequiredBean(Class<T> beanType) throws BeansException {
        return getContext().getBean(beanType);
    }

    /**
     * 根据bean的名称和类型获取bean实例
     *
     * @param beanType bean的类型
     * @return bean实例，该bean不存在时返回null
     */
    @Nullable
    public static <T> T getBean(Class<T> beanType) {
        try {
            return getRequiredBean(beanType);
        } catch (BeansException e) {
            return null;
        }
    }

    /**
     * 根据bean的名称和类型获取bean实例，不会返回{@code null}
     *
     * @param name     bean的名称
     * @param beanType bean的类型
     * @return bean实例，不为null
     * @throws BeansException 如果该bean不存在
     */
    public static <T> T getRequiredBean(String name, Class<T> beanType) throws BeansException {
        return getContext().getBean(name, beanType);
    }

    /**
     * 根据bean的名称和类型获取bean实例
     *
     * @param name     bean的名称
     * @param beanType bean的类型
     * @return bean实例，该bean不存在时返回null
     */
    @Nullable
    public static <T> T getBean(String name, Class<T> beanType) {
        try {
            return getRequiredBean(name, beanType);
        } catch (BeansException e) {
            return null;
        }
    }
}
