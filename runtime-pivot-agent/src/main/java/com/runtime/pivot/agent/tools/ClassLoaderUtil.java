package com.runtime.pivot.agent.tools;

import java.lang.instrument.Instrumentation;
import java.util.*;

public class ClassLoaderUtil {

    public static class ClassLoadInfo {
        ClassLoader classLoader;
        List<ClassLoadInfo> children = new ArrayList<>();
        List<Class<?>> loadedClasses = new ArrayList<>();

        public ClassLoadInfo(ClassLoader classLoader) {
            this.classLoader = classLoader;
        }
    }

    // 方法1: 传入 Instrumentation, 返回封装好的 ClassLoadInfo 列表
    public static List<ClassLoadInfo> getClassLoaderTree(Instrumentation inst) {
        Map<ClassLoader, ClassLoadInfo> map = new HashMap<>();
        ClassLoadInfo bootstrapInfo = new ClassLoadInfo(null);  // 引导类加载器
        map.put(null, bootstrapInfo);

        for (Class<?> clazz : inst.getAllLoadedClasses()) {
            ClassLoader loader = clazz.getClassLoader();
            map.computeIfAbsent(loader, ClassLoadInfo::new).loadedClasses.add(clazz);
        }

        Queue<ClassLoader> queue = new LinkedList<>(map.keySet());
        while (!queue.isEmpty()) {
            ClassLoader loader = queue.poll();
            if (loader == null) {
                continue;
            }
            ClassLoader parent = loader.getParent();
            if (parent != null && !map.containsKey(parent)) {
                ClassLoadInfo parentInfo = new ClassLoadInfo(parent);
                map.put(parent, parentInfo);
                queue.add(parent);
            }
            map.get(parent).children.add(map.get(loader));
        }

        return Collections.singletonList(bootstrapInfo);
    }

    // 方法2: 打印类加载器树结构
    public static void printClassLoaderTree(List<ClassLoadInfo> classLoadInfoList) {
        for (ClassLoadInfo info : classLoadInfoList) {
            printClassLoaderTree(info);
        }
    }

    private static void printClassLoaderTree(ClassLoadInfo info) {
        Deque<Map.Entry<ClassLoadInfo, String>> stack = new ArrayDeque<>();
        stack.push(new AbstractMap.SimpleEntry<>(info, ""));
        while (!stack.isEmpty()) {
            Map.Entry<ClassLoadInfo, String> entry = stack.pop();
            ClassLoadInfo currentInfo = entry.getKey();
            String prefix = entry.getValue();
            System.out.println(prefix + (currentInfo.classLoader == null ? "└── BootstrapClassLoader" : "└── " + currentInfo.classLoader));
            for (int i = currentInfo.children.size() - 1; i >= 0; i--) {
                ClassLoadInfo child = currentInfo.children.get(i);
                stack.push(new AbstractMap.SimpleEntry<>(child, prefix + "    "));
            }
        }
    }

    // 方法3: 打印类加载器树结构并打印每个类加载器加载的类列表
    public static void printClassLoaderClassTree(List<ClassLoadInfo> classLoadInfoList) {
        for (ClassLoadInfo info : classLoadInfoList) {
            printClassLoaderClassTree(info);
        }
    }

    private static void printClassLoaderClassTree(ClassLoadInfo info) {
        Deque<Map.Entry<ClassLoadInfo, String>> stack = new ArrayDeque<>();
        stack.push(new AbstractMap.SimpleEntry<>(info, ""));
        while (!stack.isEmpty()) {
            Map.Entry<ClassLoadInfo, String> entry = stack.pop();
            ClassLoadInfo currentInfo = entry.getKey();
            String prefix = entry.getValue();
            System.out.println(prefix + (currentInfo.classLoader == null ? "└── BootstrapClassLoader" : "└── " + currentInfo.classLoader));
            for (Class<?> clazz : currentInfo.loadedClasses) {
                System.out.println(prefix + "    " + clazz.getName());
            }
            for (int i = currentInfo.children.size() - 1; i >= 0; i--) {
                ClassLoadInfo child = currentInfo.children.get(i);
                stack.push(new AbstractMap.SimpleEntry<>(child, prefix + "    "));
            }
        }
    }

    // 方法4: 获取每个类加载器加载的所有类，包括引导类加载器
    public static Map<ClassLoader, List<Class<?>>> getAllLoadedClasses(Instrumentation inst) {
        Map<ClassLoader, List<Class<?>>> map = new HashMap<>();
        for (Class<?> clazz : inst.getAllLoadedClasses()) {
            ClassLoader loader = clazz.getClassLoader();
            if (loader == null) {
                loader = ClassLoader.getSystemClassLoader().getParent(); // 引导类加载器
            }
            map.computeIfAbsent(loader, k -> new ArrayList<>()).add(clazz);
        }
        return map;
    }

    public static void main(String[] args) {
        Instrumentation instrumentation = null;
        List<ClassLoaderUtil.ClassLoadInfo> classLoaderTree = ClassLoaderUtil.getClassLoaderTree(instrumentation);
        System.out.println("ClassLoader Tree:");
        ClassLoaderUtil.printClassLoaderTree(classLoaderTree);
        System.out.println("\nClassLoader Class Tree:");
        ClassLoaderUtil.printClassLoaderClassTree(classLoaderTree);
    }
}

