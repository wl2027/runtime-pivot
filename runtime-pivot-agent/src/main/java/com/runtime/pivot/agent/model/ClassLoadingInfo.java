package com.runtime.pivot.agent.model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ClassLoadingInfo {
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    private ClassLoader classLoader;
    private String className;
    private String qualifiedName;
    private Class<?> classBeingRedefined;
    private Date loadingTime;
    private String loadingTimeStr;
    private String state;//install or uninstall

    public ClassLoadingInfo(ClassLoader classLoader, String qualifiedName,String className, Class<?> classBeingRedefined) {
        this.classLoader = classLoader;
        this.className = className;
        this.qualifiedName = qualifiedName;
        this.classBeingRedefined = classBeingRedefined;
        synchronized (simpleDateFormat){
            this.loadingTime = new Date();
            //this.loadingTimeStr = DateUtil.format(loadingTime, DatePattern.NORM_DATETIME_MS_PATTERN);
            this.loadingTimeStr = simpleDateFormat.format(this.loadingTime);
        }
        this.state = "install";
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
