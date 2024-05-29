package com.runtime.pivot.agent.providers;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.runtime.pivot.agent.ActionExecutor;
import com.runtime.pivot.agent.config.AgentConstants;
import com.runtime.pivot.agent.model.Action;
import com.runtime.pivot.agent.model.ActionProvider;
import com.runtime.pivot.agent.model.ActionType;
import com.runtime.pivot.agent.model.ClassLoadingInfo;

import java.util.List;
import java.util.Map;

public class ClassActionProvider extends ActionProvider<ActionType.Class> {

    @Action(ActionType.Class.classLoadingProcess)
    public static void classLoadingProcess(Object object,String className) throws Exception {
        //class每次加载的时间和classLoad
        if (object!=null) {
            className = object.getClass().getName();
        }
        Map<String, List<ClassLoadingInfo>> classLoadingInfoMap = ActionExecutor.getAgentContext().getClassLoadingInfoMap();
//        String finalClassName = className;
        String finalClassName = "com/wl/apm/service/ApmDemoService";
        classLoadingInfoMap.forEach((name, classLoadingInfos)->{
            if (StrUtil.contains(name, finalClassName)) {
                printClassLoadingInfo(name,classLoadingInfos);
            }
        });
    }

    public static synchronized void printClassLoadingInfo(String className, List<ClassLoadingInfo> list) {
        if (list == null || list.isEmpty()) {
            System.out.println("No class loading info available.");
            return;
        }

        System.out.print("Class Loading Chain for: ");
        System.out.print(AgentConstants.ANSI_BOLD);
        System.out.println(className);
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


    private static void printClassProcessLink(String string, List<ClassLoadingInfo> classLoadingInfos) {

    }

    private static Class<?> getJvmClass(Object object, String className) throws ClassNotFoundException {
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
    public static void classFileDump(Object object,String className) throws Exception {
        //模糊查询
        Class<?> aClass = getJvmClass(object,className);
        ClassLoader classLoader = aClass.getClassLoader();
        //MY 记得输出classloader,如何获取另一个classloader的类文件呢?=>对象==>没有这个对象呢?==>后期考虑
    }

//    @Action(ActionType.Class.dumpClassList)
//    public static void dumpClassList(String className) {
//        //模糊查询
//
//    }
//
//    @Action(ActionType.Class.dumpObjectClass)
//    public static void dumpObjectClass(Object object) {
//        //精准查询
//    }



}
