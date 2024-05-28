package com.inte.framework.util;

import org.springframework.util.Assert;

import java.net.URL;
import java.nio.file.Paths;

/**
 * @author ck
 */
public class ClasspathUtils {

    public static String getFullPath(String path) {

        if (Paths.get(path).isAbsolute()) {
            return path;
        } else {
            URL url = ClasspathUtils.class.getClassLoader().getResource("");
            Assert.notNull(url, "该路径不存在");
            return Paths.get(url.getPath(), path).toString();
        }
    }

    private ClasspathUtils() {
    }
}
