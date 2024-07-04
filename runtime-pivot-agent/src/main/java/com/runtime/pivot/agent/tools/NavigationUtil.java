package com.runtime.pivot.agent.tools;

import java.io.File;

public class NavigationUtil {

    // 通过全类名打印可导航字符串
    public static String consoleNavigableClassName(String className) {
        String classPath = className.replace('.', '/') + ".java";
        String relativePath = "./src/" + classPath;
        File file = new File(relativePath);
        String absolutePath = file.getAbsolutePath();
        return " "+ className + " (" + file.getPath() + ") " ;
    }
}

