package com.runtime.pivot.agent.providers;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.runtime.pivot.agent.ActionExecutor;
import com.runtime.pivot.agent.config.AgentConstants;
import com.runtime.pivot.agent.model.Action;
import com.runtime.pivot.agent.ActionContext;
import com.runtime.pivot.agent.model.ActionProvider;
import com.runtime.pivot.agent.model.ActionType;
import com.runtime.pivot.agent.model.ClassLoadingInfo;
import com.runtime.pivot.agent.tools.InstrumentationUtils;
import com.runtime.pivot.agent.tools.ObjectTool;
import com.runtime.pivot.agent.transformer.ClassDumpTransformer;
import javassist.ClassPool;
import javassist.CtClass;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.lang.instrument.Instrumentation;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ClassActionProvider extends ActionProvider<ActionType.Class> {

    /**
     * 类加载过程
     * @param object
     * @param className
     * @return
     * @throws Exception
     */
    @Action(ActionType.Class.classLoadingProcess)
    public static String classLoadingProcess(Object object,String className) throws Exception {
        //class每次加载的时间和classLoad
        if (object!=null) {
            className = object.getClass().getName();
        }
        Map<String, List<ClassLoadingInfo>> classLoadingInfoMap = ActionExecutor.getAgentContext().getClassLoadingInfoMap();
        String finalClassName = className;
        classLoadingInfoMap.forEach((qualifiedName, classLoadingInfos)->{
            if (StrUtil.contains(qualifiedName, finalClassName)) {
                printClassLoadingInfo(qualifiedName,classLoadingInfos);
            }
        });
        return className+" loading process has been printed on the console";
    }

    public static synchronized void printClassLoadingInfo(String qualifiedName, List<ClassLoadingInfo> list) {
        if (list == null || list.isEmpty()) {
            System.out.println("No class loading info available.");
            return;
        }
        System.out.print("Class Loading Chain for: ");
        System.out.print(AgentConstants.ANSI_BOLD);
        System.out.println(qualifiedName);
        System.out.print(AgentConstants.RESET);
        int count = 1;
        for (Object info : list) {
            System.out.println(String.format(
                    "%d. Time: %s | State: %s | ClassLoader: %s",
                    count,
                    ReflectUtil.invoke(info,"getLoadingTimeStr"),
                    ReflectUtil.invoke(info,"getState"),
                    ReflectUtil.invoke(info,"getClassLoader")
            ));
            count++;
        }
        System.out.println();
    }

    private static Class<?> getJvmMainClass(Object object, String className) throws ClassNotFoundException {
        if (object!=null) {
            Class<?> aClass = object.getClass();
            return aClass;
        }else if (StrUtil.isNotEmpty(className)){
            Class<?> aClass = ActionExecutor.getActionClassLoader().loadClass(className);
            return aClass;
        }else {
            return null;
        }
    }

    @Action(ActionType.Class.classFileDump)
    public static String classFileDump(Object object,String className,String path) throws Exception {
        ActionContext actionContext = ActionExecutor.getActionContext();
        String dateFileString = actionContext.getDateFileString();
        //模糊查询
        //Class<?> aClass = getJvmClass(object,className);
        //ClassLoader classLoader = aClass.getClassLoader();
        //MY 记得输出classloader,如何获取另一个classloader的类文件呢?=>对象==>没有这个对象呢?==>后期考虑
        ClassLoader classLoader = null;
        if (object!=null) {
            Class<?> aClass = object.getClass();
            className = aClass.getName();
            classLoader = aClass.getClassLoader();
        }
        if (StrUtil.isEmpty(className)) {
            return "className is null";
        }
        Set<Class<?>> classes = new HashSet<>();
        Instrumentation instrumentation = ActionExecutor.getAgentContext().getInstrumentation();
        Class[] allLoadedClasses = instrumentation.getAllLoadedClasses();
        for (Class allLoadedClass : allLoadedClasses) {
            if (StrUtil.contains(allLoadedClass.getName(),className)) {
                if (object != null) {
                    if (classLoader.equals(allLoadedClass.getClassLoader())){
                        classes.add(allLoadedClass);
                    }
                }else {
                    classes.add(allLoadedClass);
                }
            }
        }
        ClassDumpTransformer transformer = new ClassDumpTransformer(classes);
        InstrumentationUtils.retransformClasses(instrumentation, transformer, classes);
        Map<Class<?>, byte[]> classByteMap = transformer.getClassByteMap();
        ClassPool pool = new ClassPool();
        classByteMap.forEach(((aClass, bytes) -> {
            CtClass ctClass = null;
            try {
                ctClass = pool.makeClass(new ByteArrayInputStream(bytes));
                String dumpPath = path + AgentConstants.PATH + File.separator + ActionType.Class.classFileDump + File.separator + dateFileString + File.separator + "CL" + ObjectTool.getHexId(aClass.getClassLoader());
                ctClass.debugWriteFile(dumpPath);
                String filename = dumpPath + File.separatorChar
                        + ctClass.getName().replace('.', File.separatorChar) + ".class";
                System.out.println("ClassName:"+ctClass.getName()+"\nClassLoader:"+aClass.getClassLoader()+"\nDumpPath:"+filename+"\n");
                //String filename = directoryName + File.separatorChar + classname.replace('.', File.separatorChar) + ".class";
                //
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }));
        return className+" dump path has been printed on the console";
    }
}
