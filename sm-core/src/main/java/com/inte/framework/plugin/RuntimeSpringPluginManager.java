package com.inte.framework.plugin;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.RandomUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.pf4j.*;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.messaging.handler.HandlerMethod;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.handler.AbstractHandlerMethodMapping;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 运行时动态加载和卸载插件的{@link org.pf4j.PluginManager}
 *
 * @author ck
 */


@Slf4j
@Getter
public class RuntimeSpringPluginManager extends DefaultPluginManager {

    private final ApplicationContext context;

    private final DefaultListableBeanFactory beanFactory;


    public RuntimeSpringPluginManager(ApplicationContext context, Path... pluginsRoots) {
        super(pluginsRoots);
        this.context = context;
        this.beanFactory = (DefaultListableBeanFactory) ((ConfigurableApplicationContext) context).getBeanFactory();
        this.extensionFactory = new RuntimeSpringExtensionFactory(this, context);
    }


    @Override
    public void loadPlugins() {
        super.loadPlugins();

        getPlugins().forEach(plugin -> {
            if (log.isDebugEnabled()) {
                log.debug("Registering extensions of the plugin '{}' as beans", plugin.getPluginId());
            }
//            publishEvent.publish(plugin);
            //注册到spring容器
            ClassLoader classLoader = plugin.getPluginClassLoader();
            getExtensionClassNames(plugin.getPluginId()).forEach(name -> {
                try {
                    registerExtension(name, classLoader);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        });


    }


    @Override
    public void unloadPlugins() {
        Set<String> extensionClassNames = Optional.ofNullable(getPlugins()).orElse(new ArrayList<>()).stream()
                .map(PluginWrapper::getPluginId)
                .map(this::getExtensionClassNames)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());

        super.unloadPlugins();

        if (!extensionClassNames.isEmpty()) {
            extensionClassNames.forEach(this::unregisterExtension);
        }
    }

    @Override
    public String loadPlugin(Path pluginPath) {

        String pluginId = super.loadPlugin(pluginPath);
        PluginWrapper plugin = getPlugin(pluginId);
        if (plugin != null) {
            Set<String> extensionClassNames = getExtensionClassNames(pluginId);
            if (CollectionUtils.isNotEmpty(extensionClassNames)) {
                ClassLoader classLoader = plugin.getPluginClassLoader();
                extensionClassNames.forEach(name -> {
                    try {
                        registerExtension(name, classLoader);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        }

        return pluginId;
    }

    @Override
    protected boolean unloadPlugin(String pluginId, boolean unloadDependents) {

        Set<String> extensionClassNames = getExtensionClassNames(pluginId);
        boolean unloaded = super.unloadPlugin(pluginId, unloadDependents);

        if (unloaded && !CollectionUtil.isEmpty(extensionClassNames)) {
            extensionClassNames.forEach(this::unregisterExtension);
        }

        return unloaded;
    }

    @Override
    protected ExtensionFactory createExtensionFactory() {
        return this.extensionFactory;
    }

    /**
     * 忽略目录
     */
    @Override
    protected ExtensionFinder createExtensionFinder() {
        return new IgnoreClasspathExtensionFinder(this);
    }

    private void unregisterExtension(String extensionClassName) {
        try {
            beanFactory.removeBeanDefinition(extensionClassName);
        } catch (NoSuchBeanDefinitionException e) {
            // ignore
        }
    }

    protected void registerExtension(String extensionClassName, ClassLoader classLoader) throws Exception {

        if (log.isDebugEnabled()) {
            log.debug("Register extension '{}' as bean", extensionClassName);
        }
        Class<?> extensionClass;
        try {
            extensionClass = classLoader.loadClass(extensionClassName);
        } catch (ClassNotFoundException e) {
            log.error(e.getMessage(), e);
            return;
        }

        if (context.getBeansOfType(extensionClass).isEmpty()) {

            BeanDefinition definition = BeanDefinitionBuilder.genericBeanDefinition(extensionClass)
                    .getRawBeanDefinition();
            beanFactory.registerBeanDefinition(extensionClass.getName(), definition);

        } else if (log.isDebugEnabled()) {
            log.debug("Bean registration aborted! Extension '{}' already existed as bean!", extensionClass.getName());
        }
    }




}





