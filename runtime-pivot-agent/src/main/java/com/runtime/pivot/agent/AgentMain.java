package com.runtime.pivot.agent;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.util.ReflectUtil;
import com.runtime.pivot.agent.model.AgentClassLoader;
import com.runtime.pivot.agent.config.AgentConstants;

import java.lang.annotation.Annotation;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AgentMain {

    public static void premain(String agentArgs, Instrumentation instrumentation) {
        // 当前线程的类加载器
        ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            //打印Banner
            printBanner();
            //加载执行器
            initActuator(originalClassLoader);
            //加载探针包
            AgentClassLoader agentClassLoader = initAgentClassLoader(originalClassLoader);
            //初始化上下文
            Class agentContextClass = initAgentContext(instrumentation, agentClassLoader);
            //初始化数据
            Map<String, Method> actionTypeMethodMap = initAgentData(agentClassLoader, agentContextClass);
        }  catch (Exception exception){
            //打印错误信息
            printError(exception);
        } finally {
            // 恢复原始的类加载器
            Thread.currentThread().setContextClassLoader(originalClassLoader);
        }
    }

    private static void initActuator(ClassLoader classLoader) throws Exception{
        Class<?> agentMainClass = classLoader.loadClass("com.runtime.pivot.agent.AgentMain");
        Class<?> agentContextClass = classLoader.loadClass("com.runtime.pivot.agent.AgentContext");
        Class<?> actionExecutorClass = classLoader.loadClass("com.runtime.pivot.agent.ActionExecutor");
        if (AgentConstants.DEBUG) {
            System.out.println(classLoader);
        }
    }

    private static void printError(Exception exception) {
        System.out.println(AgentConstants.ANSI_BOLD);
        System.out.println(AgentConstants.RED);
        System.out.println("Runtime Pivot Agent failed to start");
        System.out.println("error message: "+exception.getMessage());
        System.out.println(AgentConstants.RESET);
    }

    private static AgentClassLoader initAgentClassLoader(ClassLoader classLoader) {
        String agentPath = System.getProperty(AgentConstants.AGENT_PATH);
        AgentClassLoader agentClassLoader = new AgentClassLoader(agentPath);
        List<Class<?>> classes = agentClassLoader.loadJarClassList(agentPath);
        if (AgentConstants.DEBUG) {
            for (Class<?> aClass : classes) {
                System.out.println(aClass);
            }
        }
        return agentClassLoader;
    }

    private static Map<String, Method> initAgentData(AgentClassLoader agentClassLoader,Class agentContextClass) throws Exception{
        Class<Annotation> actionAnnotationClass = (Class<Annotation>) agentClassLoader.loadClass("com.runtime.pivot.agent.model.Action");
        List<Class> actionClassList = agentClassLoader.getActionClassList();
        Map<String, Method> actionTypeMethodMap = new HashMap<>();
        for (Class actionClass : actionClassList) {
            Method[] methods = ReflectUtil.getMethods(actionClass);
            for (Method method : methods) {
                method.setAccessible(true);
                Annotation annotation = method.getAnnotation(actionAnnotationClass);
                if (annotation!=null) {
                    String annotationValue = AnnotationUtil.getAnnotationValue(method, actionAnnotationClass);
                    actionTypeMethodMap.put(annotationValue,method);
                }
            }
        }
        //agentContextClass.getDeclaredField("ACTION_TYPE_METHOD_MAP").get(null); //13
        ReflectUtil.setFieldValue(agentContextClass,"ACTION_TYPE_METHOD_MAP",actionTypeMethodMap);
        return actionTypeMethodMap;
    }

    private static Class initAgentContext(Instrumentation instrumentation, AgentClassLoader agentClassLoader) throws Exception{
        Class<?> agentContextClass = agentClassLoader.loadClass("com.runtime.pivot.agent.AgentContext");
        AgentContext.INSTRUMENTATION = instrumentation;
        AgentContext.AGENT_CLASSLOADER = agentClassLoader;
        ReflectUtil.setFieldValue(agentContextClass,"INSTRUMENTATION",instrumentation);
        if (AgentConstants.DEBUG) {
            System.out.println(ReflectUtil.getFieldValue(agentContextClass, "INSTRUMENTATION"));
        }
        return agentContextClass;
    }

    private static void printBanner() {
        System.out.println(AgentConstants.ANSI_BOLD);
        System.out.println(AgentConstants.BANNER);
        System.out.println(AgentConstants.IDENTIFICATION);
        System.out.println(AgentMain.class.getClassLoader());
        System.out.println(AgentConstants.RESET);
    }

    public static void main(String[] args) {
        System.out.println(AgentConstants.ANSI_BOLD);
        System.out.println(AgentConstants.BANNER);
        System.out.println(AgentConstants.IDENTIFICATION);
        System.out.println(AgentConstants.RESET);
    }
}