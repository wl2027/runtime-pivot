package com.runtime.pivot.agent.tools;

import com.runtime.pivot.agent.model.ClassLoaderInfo;

import java.lang.instrument.Instrumentation;
import java.util.*;

public class ClassLoaderUtil {

    // 方法1: 传入 Instrumentation, 返回封装好的 ClassLoadInfo 列表
    public static List<ClassLoaderInfo> getClassLoaderTree(Instrumentation inst) {
        Map<ClassLoader, ClassLoaderInfo> map = new HashMap<>();
        ClassLoaderInfo bootstrapInfo = new ClassLoaderInfo(null);  // 引导类加载器
        map.put(null, bootstrapInfo);

        for (Class<?> clazz : inst.getAllLoadedClasses()) {
            ClassLoader loader = clazz.getClassLoader();
            map.computeIfAbsent(loader, ClassLoaderInfo::new).getLoadedClasses().add(clazz);
        }

        Queue<ClassLoader> queue = new LinkedList<>(map.keySet());
        while (!queue.isEmpty()) {
            ClassLoader loader = queue.poll();
            if (loader == null) {
                continue;
            }
            ClassLoader parent = loader.getParent();
            if (parent != null && !map.containsKey(parent)) {
                ClassLoaderInfo parentInfo = new ClassLoaderInfo(parent);
                map.put(parent, parentInfo);
                queue.add(parent);
            }
            map.get(parent).getChildren().add(map.get(loader));
        }

        return Collections.singletonList(bootstrapInfo);
    }

    // 方法2: 打印类加载器树结构
    public static void printClassLoaderTree(List<ClassLoaderInfo> classLoadInfoList) {
        for (ClassLoaderInfo info : classLoadInfoList) {
            printClassLoaderTree(info);
        }
    }

    private static void printClassLoaderTree(ClassLoaderInfo info) {
        Deque<Map.Entry<ClassLoaderInfo, String>> stack = new ArrayDeque<>();
        stack.push(new AbstractMap.SimpleEntry<>(info, ""));
        while (!stack.isEmpty()) {
            Map.Entry<ClassLoaderInfo, String> entry = stack.pop();
            ClassLoaderInfo currentInfo = entry.getKey();
            String prefix = entry.getValue();
            System.out.println(prefix + (currentInfo.getClassLoader() == null ? "└── BootstrapClassLoader" : "└── " + currentInfo.getClassLoader()));
            for (int i = currentInfo.getChildren().size() - 1; i >= 0; i--) {
                ClassLoaderInfo child = currentInfo.getChildren().get(i);
                stack.push(new AbstractMap.SimpleEntry<>(child, prefix + "    "));
            }
        }
    }

    // 方法3: 打印类加载器树结构并打印每个类加载器加载的类列表
    public static void printClassLoaderClassTree(List<ClassLoaderInfo> classLoadInfoList) {
        for (ClassLoaderInfo info : classLoadInfoList) {
            printClassLoaderClassTree(info);
        }
    }

    private static void printClassLoaderClassTree(ClassLoaderInfo info) {
        Deque<Map.Entry<ClassLoaderInfo, String>> stack = new ArrayDeque<>();
        stack.push(new AbstractMap.SimpleEntry<>(info, ""));
        while (!stack.isEmpty()) {
            Map.Entry<ClassLoaderInfo, String> entry = stack.pop();
            ClassLoaderInfo currentInfo = entry.getKey();
            String prefix = entry.getValue();
            System.out.println(prefix + (currentInfo.getClassLoader() == null ? "└── BootstrapClassLoader" : "└── " + currentInfo.getClassLoader()));
            for (Class<?> clazz : currentInfo.getLoadedClasses()) {
                System.out.println(prefix + "    " + clazz.getName());
            }
            for (int i = currentInfo.getChildren().size() - 1; i >= 0; i--) {
                ClassLoaderInfo child = currentInfo.getChildren().get(i);
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
        List<ClassLoaderInfo> classLoaderTree = ClassLoaderUtil.getClassLoaderTree(instrumentation);
        System.out.println("ClassLoader Tree:");
        ClassLoaderUtil.printClassLoaderTree(classLoaderTree);
        System.out.println("\nClassLoader Class Tree:");
        ClassLoaderUtil.printClassLoaderClassTree(classLoaderTree);
    }
}

