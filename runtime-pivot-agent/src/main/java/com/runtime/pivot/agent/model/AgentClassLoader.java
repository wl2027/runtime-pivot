package com.runtime.pivot.agent.model;

import com.runtime.pivot.agent.tools.FileTool;
import com.runtime.pivot.agent.tools.StringTool;

import java.io.File;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class AgentClassLoader extends ClassLoader{

    private volatile static AgentClassLoader agentClassLoader;
    //see JAVA SPI
    private ClassLoader currentClassLoader;
    private ClassLoader agentJarClassLoader;
    private static final List<String> shareClassNameList = new ArrayList<>();
    private final Map<String, Class<?>> loadedClasses = new ConcurrentHashMap<>();

    //用于提前action内部反射缓存
    private List<Class<?>> actionClassList = new ArrayList<>();

    static {
        //开启并行加载
        registerAsParallelCapable();
        //注册共享加载类名
        shareClassNameList.add("com.runtime.pivot.agent.AgentMain");
        shareClassNameList.add("com.runtime.pivot.agent.AgentContext");
        shareClassNameList.add("com.runtime.pivot.agent.ActionExecutor");
        shareClassNameList.add("com.runtime.pivot.agent.ActionContext");
    }

    /**
     * 获取单例agentClassLoader
     * @param jarPath
     * @return
     */
    public static AgentClassLoader getInstance(String jarPath){
        if (null == agentClassLoader) {
            synchronized (AgentClassLoader.class){
                if (null == agentClassLoader){
                    agentClassLoader = new AgentClassLoader(jarPath);
                }
            }
        }
        return agentClassLoader;
    }

    /**
     * 先加载jar包,后用共享的class覆盖
     * @param jarPath
     */
    private AgentClassLoader(String jarPath) {
        try {
            loadJarClassList(jarPath);
            loadAgentShareClassList();
        }catch (Exception exception){
            throw new RuntimeException(exception);
        }
    }

    private void loadAgentShareClassList() throws Exception{
        ClassLoader classLoader = currentClassLoader;
        if (classLoader == null) {
            classLoader = ClassLoader.getSystemClassLoader();
        }
        for (String className : shareClassNameList) {
            Class<?> aClass = classLoader.loadClass(className);
            loadedClasses.put(className,aClass);
        }
    }

    private void loadJarClassList(String jarPath) throws Exception {
        List<String> jarPathList = new ArrayList<>();
        //1.添加agent路径
        jarPathList.add("file:"+jarPath);
        //2.添加agent内jar路径
        String jarPathStr = "jar:file:" + jarPath + "!/";
        URL libUrl = new URL(jarPathStr);
        URLConnection urlConnection = libUrl.openConnection();
        if (urlConnection instanceof JarURLConnection) {
            JarURLConnection jarURLConnection = (JarURLConnection) urlConnection;
            JarFile jarFile = jarURLConnection.getJarFile();
            Enumeration<JarEntry> entries = jarFile.entries();
            String parentPath = Paths.get(jarPath).getParent().toString();
            File libDir = Paths.get(parentPath, "lib").toFile();
            libDir.deleteOnExit();
            if (!libDir.exists() && !libDir.mkdirs()) {
                System.out.println("Failed to create lib directory: " + libDir.getPath());
            }
            while (entries.hasMoreElements()) {
                JarEntry jarEntry = entries.nextElement();
                String entryName = jarEntry.getName();
                if (entryName.endsWith(".jar")) {
                    try (InputStream inputStream = this.getResourceAsStream(entryName)) {
                        File target = new File(parentPath, entryName);
                        FileTool.writeFromStream(inputStream, target);
                        jarPathList.add(target.getPath());
                    }
                }
            }
            URL[] urls = new URL[jarPathList.size()];
            for (int i = 0; i < jarPathList.size(); i++) {
                urls[i] = new URL(jarPathList.get(i));
            }
            agentJarClassLoader = new URLClassLoader(urls){
                @Override
                protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
                    synchronized (getClassLoadingLock(name)) {
                        //shareClass直接从系统类加载器加载
                        if (shareClassNameList.contains(name)) {
                            return getCurrentClassLoader().loadClass(name);
                        }
                        // First, check if the class has already been loaded
                        Class<?> c = findLoadedClass(name);
                        if (c == null) {
                            try {
                                if (c == null) {
                                    c = findClass(name);
                                }
                            }catch (Exception exception){
                                c = getCurrentClassLoader().loadClass(name);
                            }
                        }
                        if (resolve) {
                            resolveClass(c);
                        }
                        return c;
                    }
                }
            };
        }
        //加载整个agent的class并缓存
        List<String> classNames = new ArrayList<>();
        JarFile jarFile = new JarFile(jarPath);
        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry jarEntry = entries.nextElement();
            String entryName = jarEntry.getName();
            if (entryName != null && entryName.endsWith(".class")) {
                entryName = entryName.replace("/", ".").substring(0, entryName.lastIndexOf("."));
                classNames.add(entryName);
            }
        }
        for (String className : classNames) {
            if (StringTool.isNotEmpty(className)) {
                if (!StringTool.contains(className, "org.apache.lucene") ||
                        StringTool.startWith(className, "org.apache.lucene.util") ||
                        StringTool.startWith(className, "org.apache.lucene.search") ||
                        StringTool.startWith(className, "org.apache.lucene.index")) {
                    Class<?> clazz = agentJarClassLoader.loadClass(className);
                    loadedClasses.put(className,clazz);
                    if (StringTool.startWith(className, "com.runtime.pivot.agent.providers")) {
                        actionClassList.add(clazz);
                    }
                }
            }
        }

    }


    /**
     * 破坏双亲委派
     * 按以下顺序搜索类：
     * 1. 是否为缓存内
     * 2. 调用当前类加载器=>双亲委派
     * @param name 类名
     * @param resolve 如果为true,则解析类,对生成的 Class 对象调用该resolveClass(Class)方法
     * @return 生成的 Class 对象
     * @throws ClassNotFoundException
     */
    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        Class<?> aClass = loadedClasses.get(name);
        if (aClass != null) {
            return aClass;
        }
        ClassLoader classLoader = getCurrentClassLoader();
        aClass = classLoader.loadClass(name);
        return aClass;
    }
    public void setCurrentClassLoader(ClassLoader currentClassLoader) {
        this.currentClassLoader = currentClassLoader;
    }

    public ClassLoader getCurrentClassLoader() {
        return currentClassLoader == null ? ClassLoader.getSystemClassLoader() : currentClassLoader;
    }

    public Map<String, Class<?>> getLoadedClasses() {
        return loadedClasses;
    }

    public List<Class<?>> getActionClassList() {
        return actionClassList;
    }
}


//package com.runtime.pivot.agent.model;
//
//        import com.runtime.pivot.agent.ActionContext;
//        import com.runtime.pivot.agent.ActionExecutor;
//        import com.runtime.pivot.agent.AgentContext;
//        import com.runtime.pivot.agent.tools.FileTool;
//        import com.runtime.pivot.agent.tools.StringTool;
//
//        import java.io.File;
//        import java.io.IOException;
//        import java.io.InputStream;
//        import java.lang.reflect.InvocationTargetException;
//        import java.lang.reflect.Method;
//        import java.net.JarURLConnection;
//        import java.net.MalformedURLException;
//        import java.net.URL;
//        import java.net.URLClassLoader;
//        import java.net.URLConnection;
//        import java.nio.file.Paths;
//        import java.util.ArrayList;
//        import java.util.Enumeration;
//        import java.util.List;
//        import java.util.jar.JarEntry;
//        import java.util.jar.JarFile;
//
//public class AgentClassLoader extends URLClassLoader {
//
//    private static final List<String> shareClassList = new ArrayList();
//
//    static {
//        registerAsParallelCapable();
//        shareClassList.add(ActionExecutor.class.getName());
//        shareClassList.add(AgentContext.class.getName());
//        shareClassList.add(ActionContext.class.getName());
//    }
//
//    @Override
//    protected Class<?> findClass(String name) throws ClassNotFoundException {
//        //不破坏双亲委派,只修改加载类逻辑
//        if (shareClassList.contains(name)) {
//            ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
//            return systemClassLoader.loadClass(name);
//        }
//        Class<?> aClass = null;
//        try{
//            aClass = super.findClass(name);
//        }catch (ClassNotFoundException exception){
//            //多个参数当前只支持去第一个参数的类加载器去找
//            ClassLoader actionClassLoader = ActionExecutor.getActionClassLoader();
//            if (actionClassLoader == null) {
//                //null or 根加载器
//                throw new RuntimePivotException("args' agentClassLoader is null!");
//            }
//            aClass = actionClassLoader.loadClass(name);
//        }
//        return aClass;
//    }
//
//    private List<Class> actionClassList = new ArrayList<>();
//
//    public List<Class> getActionClassList() {
//        return actionClassList;
//    }
//
//    public void setActionClassList(List<Class> actionClassList) {
//        this.actionClassList = actionClassList;
//    }
//
//    public AgentClassLoader(String url) {
//        super(new URL[]{},null);
//        Method method;
//        try {
//            method = URLClassLoader.class.getDeclaredMethod("addURL", new Class[]{URL.class});
//            if (!method.isAccessible()) {
//                method.setAccessible(true);
//            }
//            method.invoke(this, new URL("file:" + url));
//        } catch (NoSuchMethodException | MalformedURLException | InvocationTargetException | IllegalAccessException e) {
//            throw new RuntimeException(e);
//        }
//        // write to local lib
//        try {
//            String jarResourceStr = "jar:file:" + url + "!/";
//            URL libUrl = new URL(jarResourceStr);
//            URLConnection urlConnection = libUrl.openConnection();
//            if (urlConnection instanceof JarURLConnection) {
//                JarURLConnection jarURLConnection = (JarURLConnection) urlConnection;
//                JarFile jarFile = jarURLConnection.getJarFile();
//                Enumeration<JarEntry> entries = jarFile.entries();
//                String parentPath = Paths.get(url).getParent().toString();
//                File libDir = Paths.get(parentPath, "lib").toFile();
//                libDir.deleteOnExit();
//                boolean isSuccess = libDir.mkdirs();
//                //MY E:\002_Code\000_github\IDEA\runtime-pivot\runtime-pivot-agent\build\libs\lib
//                if (!isSuccess&&!libDir.exists()) {
//                    System.out.println("create lib dir fail:"+libDir.getPath());
//                }
//                while (entries.hasMoreElements()) {
//                    JarEntry jarEntry = entries.nextElement();
//                    String entryName = jarEntry.getName();
//                    if (entryName.endsWith(".jar")) {
//                        try (InputStream inputStream = this.getResourceAsStream(entryName)) {
//                            File target = new File(parentPath, entryName);
//                            //FileUtil.writeFromStream(inputStream, target);
//                            FileTool.writeFromStream(inputStream,target);
//                            method.invoke(this, new URL("file:" + target.getPath()));
//                        } catch (IOException e) {
//                            throw new RuntimeException(e);
//                        } catch (InvocationTargetException e) {
//                            throw new RuntimeException(e);
//                        } catch (IllegalAccessException e) {
//                            throw new RuntimeException(e);
//                        }
//                    }
//                }
//            }
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    public List<Class<?>> loadJarClassList(String url) {
//        AgentClassLoader classLoader = this;
//        List<Class<?>> classList = new ArrayList<>();
//        List<String> classNames = new ArrayList<>();
//        try (JarFile jarFile = new JarFile(url)) {
//            Enumeration<JarEntry> entries = jarFile.entries();
//            while (entries.hasMoreElements()) {
//                JarEntry jarEntry = entries.nextElement();
//                String entryName = jarEntry.getName();
//                if (entryName != null && entryName.endsWith(".class")) {
//                    entryName = entryName.replace("/", ".").substring(0, entryName.lastIndexOf("."));
//                    classNames.add(entryName);
//                }
//            }
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//
//        if (classNames.size() > 0) {
//            for (String className : classNames) {
//                try {
//                    if (StringTool.isNotEmpty(className)) {
//                        if (!StringTool.contains(className,"org.apache.lucene")
//                                || StringTool.startWith(className,"org.apache.lucene.util")
//                                || StringTool.startWith(className,"org.apache.lucene.search")
//                                || StringTool.startWith(className,"org.apache.lucene.index")
//                        ){
//                            Class<?> theClass = classLoader.loadClass(className);
//                            classList.add(theClass);
//                            if (StringTool.startWith(className,"com.runtime.pivot.agent.providers")) {
//                                actionClassList.add(theClass);
//                            }
//                        }
//                    }
//                } catch (Exception e) {
//                    throw new RuntimeException(e);
//                }
//            }
//        }
//        return classList;
//    }
//
//
//
//}
