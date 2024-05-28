package com.runtime.pivot.agent.model;

import java.security.ProtectionDomain;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ClassLoadingInfo {
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    private final ClassLoader classLoader;
    private final String className;
    private final Class<?> classBeingRedefined;
    private final ProtectionDomain protectionDomain;
    private final byte[] classfileBuffer;
    private final Date loadingTime;
    private final String loadingTimeStr;
    private final String state;//open or close

    public ClassLoadingInfo(ClassLoader classLoader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        this.classLoader = classLoader;
        this.className = className;
        this.classBeingRedefined = classBeingRedefined;
        this.protectionDomain = protectionDomain;
        this.classfileBuffer = classfileBuffer;
        synchronized (simpleDateFormat){
            this.loadingTime = new Date();
            //this.loadingTimeStr = DateUtil.format(loadingTime, DatePattern.NORM_DATETIME_MS_PATTERN);
            this.loadingTimeStr = simpleDateFormat.format(this.loadingTime);
        }
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
