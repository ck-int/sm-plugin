package com.inte.framework.autoconfigure.plugin;

import com.inte.framework.plugin.PluginManagerFileSystemListener;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.HiddenFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.pf4j.PluginManager;
import org.pf4j.PluginWrapper;
import org.springframework.boot.CommandLineRunner;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadFactory;

/**
 * 控制{@link PluginManager}以及文件监听器{@link FileAlterationListener}, 达到应用在运行时加载以及卸载插件的功能
 * @author ck
 */
@Slf4j
public class FileSystemMonitorCommandLineRunner implements CommandLineRunner {

    private final FileAlterationListener listener;

    private final PluginManager manager;

    private ThreadFactory threadFactory;

    /**
     * 启动时自动注入FileSystemMonitorCommandLineRunner对象
     * @param listener 文件监听器
     * @param manager 插件管理
     */
    public FileSystemMonitorCommandLineRunner(PluginManagerFileSystemListener listener, PluginManager manager) {
        this.listener = listener;
        this.manager = manager;
    }

    /**
     * 初始化
     * @param args
     */
    @Override
    public void run(String... args) {
        manager.loadPlugins();
        manager.startPlugins();
        List<PluginWrapper> plugins = manager.getPlugins();
        for (PluginWrapper plugin : plugins) {
            listener.onFileChange(plugin.getPluginPath().toFile());
        }
        IOFileFilter filter = FileFilterUtils.and(
                FileFilterUtils.fileFileFilter(),
                HiddenFileFilter.VISIBLE,
                FileFilterUtils.or(
                        FileFilterUtils.suffixFileFilter(".jar"),
                        FileFilterUtils.suffixFileFilter(".zip"))
        );
        FileAlterationObserver[] observers =  Optional.ofNullable(manager.getPluginsRoots()).orElse(new ArrayList<>())
                .stream()
                .map(Path::toFile)
                .map(root -> {
                    //开始监听配置文件中配置的路径
                    FileAlterationObserver observer = new FileAlterationObserver(root, filter);
                    observer.addListener(listener);
                    return observer;
                })
                .toArray(FileAlterationObserver[]::new);
        FileAlterationMonitor monitor = new FileAlterationMonitor(1000L, observers);
        if (threadFactory != null) {
            monitor.setThreadFactory(threadFactory);
        }

        startMonitoring(monitor);
    }

    private void startMonitoring(FileAlterationMonitor monitor) {

        try {
            monitor.start();
        } catch (Exception e) {
            log.error("插件目录监控过程出现了异常, 即将重新启动目录监控", e);
            startMonitoring(monitor);
        }
    }

    public void setThreadFactory(ThreadFactory threadFactory) {
        this.threadFactory = threadFactory;
    }
}
