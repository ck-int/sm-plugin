package com.inte.framework.autoconfigure;

import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author ck
 */
public class PackageScanRegistry {

    private final Set<String> basePackages = new TreeSet<>();

    private void addBasePackage(String basePackage) {

        Assert.hasText(basePackage, "basePackage 不能为空");
        String trimmed = basePackage.trim();

        for (String p : basePackages) {
            if (p.startsWith(trimmed)) {
                return;
            }
        }

        basePackages.add(basePackage);
    }

    public void addBasePackages(String... basePackages) {
        if (basePackages.length == 0) {
            return;
        }

        for (String basePackage : basePackages) {
            addBasePackage(basePackage);
        }
    }

    public void addBasePackages(Collection<String> basePackages) {
        if (CollectionUtils.isEmpty(basePackages)) {
            return;
        }

        basePackages.forEach(this::addBasePackage);
    }

    public Set<String> getBasePackages() {
        return Collections.unmodifiableSet(basePackages);
    }

}
