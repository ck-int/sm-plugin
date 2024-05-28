package com.inte.framework.autoconfigure.plugin;

import com.inte.framework.autoconfigure.SmFrameworkBaseConfiguration;
import com.inte.framework.plugin.PluginManagerFileSystemListener;
import com.inte.framework.plugin.RuntimeSpringPluginManager;
import com.inte.framework.util.ClasspathUtils;
import lombok.extern.slf4j.Slf4j;
import org.pf4j.PluginManager;
import org.pf4j.spring.SpringPlugin;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import java.io.File;
import java.util.Optional;
import java.util.concurrent.ThreadFactory;

/**
 * @author ck
 */
@Slf4j
@ComponentScan(PluginAutoConfiguration.BASE_PACKAGE)
@AutoConfigurationPackage(basePackages = PluginAutoConfiguration.BASE_PACKAGE)
@EnableConfigurationProperties(PluginProperties.class)
@ConditionalOnClass(SpringPlugin.class)
@Configuration(proxyBeanMethods = false)
public class PluginAutoConfiguration extends SmFrameworkBaseConfiguration {

    public static final String BASE_PACKAGE = "com.inte.framework.*";


    @Bean
    public PluginManager pluginManager(ApplicationContext context, PluginProperties properties) {
        String dir = ClasspathUtils.getFullPath(properties.getDir());
        File file = new File(dir);
        if (!file.exists()) {
            log.warn("该目录不存在: " + file.toPath());
        } else if (!file.isDirectory()) {
            throw new IllegalStateException("不是目录: " + file.getAbsolutePath());
        }

        return new RuntimeSpringPluginManager(context, file.toPath());
    }

    @Bean
    public FileSystemMonitorCommandLineRunner fileSystemMonitorCommandLineRunner(
            PluginManager manager,
            @Qualifier("applicationTaskExecutor") Optional<ThreadFactory> threadFactory) {
        PluginManagerFileSystemListener listener = new PluginManagerFileSystemListener(manager);
        FileSystemMonitorCommandLineRunner runner = new FileSystemMonitorCommandLineRunner(listener, manager);
        threadFactory.ifPresent(runner::setThreadFactory);
        return runner;
    }


}
