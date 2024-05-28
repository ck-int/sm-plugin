package com.inte.framework.autoconfigure.plugin;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author ck
 */
@Data
@ConfigurationProperties(prefix = "tsdb.inte.plugin")
public class PluginProperties {

    private String dir = "plugin";

}
