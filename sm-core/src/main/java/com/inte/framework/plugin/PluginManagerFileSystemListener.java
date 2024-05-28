package com.inte.framework.plugin;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.pf4j.PluginManager;
import org.pf4j.PluginWrapper;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

/**
 * 监听插件目录，根据不同的行为进行插件的加载和卸载
 *
 * @author ck
 */
@Slf4j
public class PluginManagerFileSystemListener extends FileAlterationListenerAdaptor {

    /**
     * 插件加载
     */
    private final PluginManager manager;

    public PluginManagerFileSystemListener(PluginManager manager) {
        this.manager = manager;
    }

    /**
     * 启动后初始化插件
     *
     * @param observer
     */
    @Override
    public void onStart(FileAlterationObserver observer) {
        if (log.isDebugEnabled()) {
            log.info("【文件监听】-插件目录开始监听目录更新");
        }
    }

    /**
     * 文件创建
     *
     * @param file
     */
    @Override
    public void onFileCreate(File file) {
        try {
            loadAndStartPlugin(file.toPath());
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 文件删除
     *
     * @param
     */
    public void fileDelete(String pluginId) {
        log.info("【文件监听】- {} 插件删除卸载",pluginId);
        //pluginId存在就卸载插件
        manager.unloadPlugin(pluginId);
    }

    @Override
    public void onFileDelete(File file) {
        findMatchesPlugin(file.toPath())
                .map(PluginWrapper::getPluginId)
                .ifPresent(item -> {
                    this.fileDelete(item);
                });
    }

    /**
     * 文件修改
     *
     * @param file
     */
    @Override
    public void onFileChange(File file) {

        Path path = file.toPath();
        Optional<PluginWrapper> matchesPlugin = findMatchesPlugin(path);
        findMatchesPlugin(path)
                .map(PluginWrapper::getPluginId)
                .ifPresent(item ->    manager.unloadPlugin(item));
        try {
            loadAndStartPlugin(path);
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }
    }

    private Optional<PluginWrapper> findMatchesPlugin(Path path) {
        //获取所有插件
        List<PluginWrapper> plugins = manager.getPlugins();
        if (CollectionUtils.isEmpty(plugins)) {
            return Optional.empty();
        }
        return plugins.stream()
                .filter(plugin -> plugin.getPluginPath().equals(path))
                .findAny();
    }

    private void loadAndStartPlugin(Path path) throws MqttException {
        String pluginId = manager.loadPlugin(path);
        manager.startPlugin(pluginId);
    }
}
