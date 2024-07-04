package com.runtime.pivot.agent.model;

import java.util.ArrayList;
import java.util.List;

public class ClassLoaderInfo {
    private ClassLoader classLoader;
    private List<ClassLoaderInfo> children = new ArrayList<>();
    private List<Class<?>> loadedClasses = new ArrayList<>();

    public ClassLoaderInfo(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public List<ClassLoaderInfo> getChildren() {
        return children;
    }

    public void setChildren(List<ClassLoaderInfo> children) {
        this.children = children;
    }

    public List<Class<?>> getLoadedClasses() {
        return loadedClasses;
    }

    public void setLoadedClasses(List<Class<?>> loadedClasses) {
        this.loadedClasses = loadedClasses;
    }
}
