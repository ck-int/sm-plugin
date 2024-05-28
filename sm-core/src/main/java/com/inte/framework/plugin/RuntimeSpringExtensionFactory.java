package com.inte.framework.plugin;

import lombok.extern.slf4j.Slf4j;
import org.pf4j.PluginManager;
import org.pf4j.spring.SpringExtensionFactory;
import org.springframework.context.ApplicationContext;

import java.util.Optional;

/**
 * 支持运行时读取Spring容器内注册好的{@link org.pf4j.ExtensionPoint}
 *@author ck
 */

public class RuntimeSpringExtensionFactory extends SpringExtensionFactory {

    private final ApplicationContext context;

    public RuntimeSpringExtensionFactory(PluginManager pluginManager, ApplicationContext context) {
        super(pluginManager);
        this.context = context;
    }

    @Override
    protected <T> Optional<ApplicationContext> getApplicationContextBy(Class<T> extensionClass) {
        return Optional.of(context);
    }

    @Override
    public <T> T create(Class<T> extensionClass) {
        return context.getBean(extensionClass.getName(), extensionClass);
    }

}
