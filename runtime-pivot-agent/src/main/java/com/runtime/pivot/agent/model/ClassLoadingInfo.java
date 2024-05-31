package com.runtime.pivot.agent.model;

import java.security.ProtectionDomain;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ClassLoadingInfo {
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    private final ClassLoader classLoader;
    private final String className;
    private final String qualifiedName;
    private final Class<?> classBeingRedefined;
    private final ProtectionDomain protectionDomain;
    private final byte[] classfileBuffer;
    private final Date loadingTime;
    private final String loadingTimeStr;
    private final String state;//open or close

    public ClassLoadingInfo(ClassLoader classLoader, String qualifiedName,String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        this.classLoader = classLoader;
        this.className = className;
        this.qualifiedName = qualifiedName;
        this.classBeingRedefined = classBeingRedefined;
        this.protectionDomain = protectionDomain;
        this.classfileBuffer = classfileBuffer;
        synchronized (simpleDateFormat){
            this.loadingTime = new Date();
            //this.loadingTimeStr = DateUtil.format(loadingTime, DatePattern.NORM_DATETIME_MS_PATTERN);
            this.loadingTimeStr = simpleDateFormat.format(this.loadingTime);
        }
        this.state = "install";//install or uninstall
    }

    public static SimpleDateFormat getSimpleDateFormat() {
        return simpleDateFormat;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public String getClassName() {
        return className;
    }

    public Class<?> getClassBeingRedefined() {
        return classBeingRedefined;
    }

    public ProtectionDomain getProtectionDomain() {
        return protectionDomain;
    }

    public byte[] getClassfileBuffer() {
        return classfileBuffer;
    }

    public Date getLoadingTime() {
        return loadingTime;
    }

    public String getLoadingTimeStr() {
        return loadingTimeStr;
    }

    public String getState() {
        return state;
    }
}
