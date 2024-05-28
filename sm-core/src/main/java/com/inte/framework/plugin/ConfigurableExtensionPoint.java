package com.inte.framework.plugin;

import org.pf4j.ExtensionPoint;

/**
 * 可配置插件接口
 *
 *@author ck
 */

public interface ConfigurableExtensionPoint extends ExtensionPoint {

    /**
     * 获取插件配置类
     *
     * @return WorkFlowPluginConfiguration
     */
    default PluginConfiguration getConfiguration() {
        return PluginConfiguration.DEFAULT;
    }

}
