package com.runtime.pivot.agent.model;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class AgentClassLoader extends URLClassLoader {

    static {
        registerAsParallelCapable();
    }

    private List<Class> actionClassList = new ArrayList<>();

    public List<Class> getActionClassList() {
        return actionClassList;
    }

    public void setActionClassList(List<Class> actionClassList) {
        this.actionClassList = actionClassList;
    }

    public AgentClassLoader(String url) {
        super(new URL[]{},null);
        Method method;
        try {
            method = URLClassLoader.class.getDeclaredMethod("addURL", new Class[]{URL.class});
            if (!method.isAccessible()) {
                method.setAccessible(true);
            }
            method.invoke(this, new URL("file:" + url));
        } catch (NoSuchMethodException | MalformedURLException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        // write to local lib
        try {
            String jarResourceStr = "jar:file:" + url + "!/";
            URL libUrl = new URL(jarResourceStr);
            URLConnection urlConnection = libUrl.openConnection();
            if (urlConnection instanceof JarURLConnection) {
                JarURLConnection jarURLConnection = (JarURLConnection) urlConnection;
                JarFile jarFile = jarURLConnection.getJarFile();
                Enumeration<JarEntry> entries = jarFile.entries();
                String parentPath = Paths.get(url).getParent().toString();
                File libDir = Paths.get(parentPath, "lib").toFile();
                libDir.deleteOnExit();
                boolean isSuccess = libDir.mkdirs();
                if (!isSuccess) {
                    System.out.println("create lib dir fail");
                }
                while (entries.hasMoreElements()) {
                    JarEntry jarEntry = entries.nextElement();
                    String entryName = jarEntry.getName();
                    if (entryName.endsWith(".jar")) {
                        try (InputStream inputStream = this.getResourceAsStream(entryName)) {
                            File target = new File(parentPath, entryName);
                            FileUtil.writeFromStream(inputStream, target);
                            method.invoke(this, new URL("file:" + target.getPath()));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        } catch (InvocationTargetException e) {
                            throw new RuntimeException(e);
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Class<?>> loadJarClassList(String url) {
        AgentClassLoader classLoader = this;
        List<Class<?>> classList = new ArrayList<>();
        List<String> classNames = new ArrayList<>();
        try (JarFile jarFile = new JarFile(url)) {
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry jarEntry = entries.nextElement();
                String entryName = jarEntry.getName();
                if (entryName != null && entryName.endsWith(".class")) {
                    entryName = entryName.replace("/", ".").substring(0, entryName.lastIndexOf("."));
                    classNames.add(entryName);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (classNames.size() > 0) {
            for (String className : classNames) {
                try {
                    if (!StrUtil.isEmpty(className)) {
                        if (!StrUtil.contains(className,"org.apache.lucene")
                                || StrUtil.startWith(className,"org.apache.lucene.util")
                                || StrUtil.startWith(className,"org.apache.lucene.search")
                                || StrUtil.startWith(className,"org.apache.lucene.index")
                        ){
                            Class<?> theClass = classLoader.loadClass(className);
                            classList.add(theClass);
                            if (StrUtil.startWith(className,"com.runtime.pivot.agent.providers")) {
                                actionClassList.add(theClass);
                            }
                        }
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return classList;
    }



}
