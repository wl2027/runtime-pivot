package com.runtime.pivot.agent.model;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;

import java.security.ProtectionDomain;
import java.util.Date;

public class ClassLoadingInfo {
    private final ClassLoader classLoader;
    private final String className;
    private Class<?> classBeingRedefined;
    private ProtectionDomain protectionDomain;
    private byte[] classfileBuffer;
    private final Date loadingTime;
    private final String loadingTimeStr;
    private final String state;//open or close

    public ClassLoadingInfo(ClassLoader classLoader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer, Date loadingTime) {
        this.classLoader = classLoader;
        this.className = className;
        this.classBeingRedefined = classBeingRedefined;
        this.protectionDomain = protectionDomain;
        this.classfileBuffer = classfileBuffer;
        this.loadingTime = loadingTime;
        this.loadingTimeStr = DateUtil.format(new Date(), DatePattern.NORM_DATETIME_MS_PATTERN);
        this.state = "open";
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public String getClassName() {
        return className;
    }

    public Date getLoadingTime() {
        return loadingTime;
    }

    public String getLoadingTimeStr() {
        return loadingTimeStr;
    }
}
