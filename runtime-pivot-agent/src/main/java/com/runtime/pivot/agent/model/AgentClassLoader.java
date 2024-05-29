package com.runtime.pivot.agent.model;

import com.runtime.pivot.agent.ActionExecutor;
import com.runtime.pivot.agent.AgentContext;
import com.runtime.pivot.agent.tools.FileTool;
import com.runtime.pivot.agent.tools.StringTool;

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

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        //不破坏双亲委派,只修改加载类逻辑
        if (ActionExecutor.class.getName().equals(name)|| AgentContext.class.getName().equals(name)) {
            ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
            return systemClassLoader.loadClass(name);
        }
        Class<?> aClass = null;
        try{
            aClass = super.findClass(name);
        }catch (ClassNotFoundException exception){
            //多个参数当前只支持去第一个参数的类加载器去找
            ClassLoader actionClassLoader = ActionExecutor.getActionClassLoader();
            if (actionClassLoader == null) {
                throw new RuntimePivotException("args' agentClassLoader is null!");
            }
            aClass = actionClassLoader.loadClass(name);
        }
        return aClass;
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
                            //FileUtil.writeFromStream(inputStream, target);
                            FileTool.writeFromStream(inputStream,target);
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
                    if (StringTool.isNotEmpty(className)) {
                        if (!StringTool.contains(className,"org.apache.lucene")
                                || StringTool.startWith(className,"org.apache.lucene.util")
                                || StringTool.startWith(className,"org.apache.lucene.search")
                                || StringTool.startWith(className,"org.apache.lucene.index")
                        ){
                            Class<?> theClass = classLoader.loadClass(className);
                            classList.add(theClass);
                            if (StringTool.startWith(className,"com.runtime.pivot.agent.providers")) {
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
