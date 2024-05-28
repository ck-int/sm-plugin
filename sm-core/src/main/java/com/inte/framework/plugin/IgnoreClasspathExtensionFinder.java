package com.inte.framework.plugin;

import org.pf4j.ExtensionWrapper;
import org.pf4j.LegacyExtensionFinder;
import org.pf4j.PluginManager;
import org.springframework.lang.Nullable;

import java.util.Collections;
import java.util.List;

/**
 * 不加载类目录下的{@link org.pf4j.ExtensionPoint}
 * @author ck
 *
 **/
public class IgnoreClasspathExtensionFinder extends LegacyExtensionFinder {

    public IgnoreClasspathExtensionFinder(PluginManager pluginManager) {
        super(pluginManager);
    }

    @Override
    public <T> List<ExtensionWrapper<T>> find(Class<T> type, @Nullable String pluginId) {
        return pluginId == null ? Collections.emptyList() : super.find(type, pluginId);
    }

}
