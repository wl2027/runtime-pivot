package com.runtime.pivot.agent;

import com.runtime.pivot.agent.model.AgentClassLoader;
import com.runtime.pivot.agent.config.AgentConstants;
import com.runtime.pivot.agent.model.ClassLoadingInfo;
import com.runtime.pivot.agent.transformer.ClassLoadingTransformer;

import java.lang.annotation.Annotation;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

public class AgentMain {

    public static volatile Map<String, List<ClassLoadingInfo>> classLoadingInfoMap = new ConcurrentSkipListMap<>();

    public static void premain(String agentArgs, Instrumentation instrumentation) {
        //注册启动失败钩子
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("JVM is shutting down... ");
        }));
        // 当前线程的类加载器
        ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();
        ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(systemClassLoader);
            //打印Banner
            printBanner();
            //初始化Transformer
            initTransformer(instrumentation);
            //加载执行器
            initActuator(systemClassLoader);
            //加载探针包
            AgentClassLoader agentClassLoader = initAgentClassLoader(originalClassLoader);
            //初始化上下文
            initAgentContext(instrumentation, agentClassLoader);
        }  catch (Exception exception){
            //打印错误信息
            printError(exception);
        } finally {
            // 恢复原始的类加载器
            Thread.currentThread().setContextClassLoader(originalClassLoader);
        }
    }

    private static void initTransformer(Instrumentation instrumentation) {
        //当加载类时，当 它们被重新定义时，转换器被调用。如果 canRetransform 为 true，则当它们 被重新转换时
        instrumentation.addTransformer(new ClassLoadingTransformer(),true);
        //test instrumentation.addTransformer(new JdbcTransformer());
    }

    private static void initActuator(ClassLoader classLoader) throws Exception{
        Class<?> agentMainClass = classLoader.loadClass("com.runtime.pivot.agent.AgentMain");
        Class<?> agentContextClass = classLoader.loadClass("com.runtime.pivot.agent.AgentContext");
        Class<?> actionExecutorClass = classLoader.loadClass("com.runtime.pivot.agent.ActionExecutor");
        Class<?> actionContextClass = classLoader.loadClass("com.runtime.pivot.agent.ActionContext");
//        if (AgentConstants.DEBUG) {
//            System.out.println(classLoader);
//        }
    }

    private static void printError(Exception exception) {
//        if (true) return;
        System.out.println(AgentConstants.ANSI_BOLD);
        System.out.println(AgentConstants.RED);
        System.out.println("Runtime Pivot Agent failed to start");
        System.out.println("error message: "+exception.getMessage());
        if (AgentConstants.DEBUG) {
            exception.printStackTrace();
        }
        System.out.println(AgentConstants.RESET);
    }

    private static AgentClassLoader initAgentClassLoader(ClassLoader classLoader) {
        String agentPath = System.getProperty(AgentConstants.AGENT_PATH);
        AgentClassLoader agentClassLoader = new AgentClassLoader(agentPath);
        List<Class<?>> classes = agentClassLoader.loadJarClassList(agentPath);
//        if (AgentConstants.DEBUG) {
//            for (Class<?> aClass : classes) {
//                System.out.println(aClass);
//            }
//        }
        return agentClassLoader;
    }

    private static Map<String, Method> initAgentData(AgentClassLoader agentClassLoader) throws Exception{
        Class<Annotation> actionAnnotationClass = (Class<Annotation>) agentClassLoader.loadClass("com.runtime.pivot.agent.model.Action");
        List<Class> actionClassList = agentClassLoader.getActionClassList();
        Map<String, Method> actionTypeMethodMap = new HashMap<>();
        for (Class actionClass : actionClassList) {
            //Method[] methods = ReflectUtil.getMethods(actionClass);
            Method[] methods = actionClass.getDeclaredMethods();
            for (Method method : methods) {
                method.setAccessible(true);
                Annotation annotation = method.getAnnotation(actionAnnotationClass);
                if (annotation!=null) {
                    Method declaredMethod = actionAnnotationClass.getDeclaredMethod("value");
                    declaredMethod.setAccessible(true);
                    String annotationValue = declaredMethod.invoke(annotation).toString();
                    //String annotationValue = AnnotationUtil.getAnnotationValue(method, actionAnnotationClass);
                    actionTypeMethodMap.put(annotationValue,method);
                }
            }
        }
        return actionTypeMethodMap;
    }

    private static void initAgentContext(Instrumentation instrumentation, AgentClassLoader agentClassLoader) throws Exception{
        Class<?> agentContextClass = agentClassLoader.loadClass("com.runtime.pivot.agent.AgentContext");
        Map<String, Method> actionTypeMethodMap = initAgentData(agentClassLoader);
        AgentContext externalAgentContext = new AgentContext();
        Object internalAgentContext = agentContextClass.newInstance();
        externalAgentContext.setInstrumentation(instrumentation);
        externalAgentContext.setAgentClassloader(agentClassLoader);
        externalAgentContext.setActionTypeMethodMap(actionTypeMethodMap);
        Map<String, List<ClassLoadingInfo>> hashMap = new ConcurrentSkipListMap<>();
        hashMap.putAll(classLoadingInfoMap);
        externalAgentContext.setClassLoadingInfoMap(hashMap);
        ActionExecutor.setAgentContext(externalAgentContext);

//        if (AgentConstants.DEBUG) {
//            System.out.println(internalAgentContext);
//        }
    }

    private static void printBanner() {
//        if (true) return;
        System.out.println(AgentConstants.ANSI_BOLD);
        System.out.println(AgentConstants.BANNER);
        System.out.println(AgentConstants.IDENTIFICATION);
        System.out.println(AgentConstants.RESET);
    }

    public static void main(String[] args) {
        System.out.println(AgentConstants.ANSI_BOLD);
        System.out.println(AgentConstants.BANNER);
        System.out.println(AgentConstants.IDENTIFICATION);
        System.out.println(AgentConstants.RESET);
    }
}