package com.runtime.pivot.agent;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class ActionExecutor {
    //内部上线文
    private static Object INTERNAL_AGENT_CONTEXT;

    //外部上下文
    private static AgentContext EXTERNAL_AGENT_CONTEXT;

    public static AgentContext getAgentContext() {
        return EXTERNAL_AGENT_CONTEXT;
    }

    public static void setAgentContext(AgentContext agentContext){
        EXTERNAL_AGENT_CONTEXT=agentContext;
    }

    public static Class<?> getExternalClass(Class<?> aClass) throws Exception{
        String className = aClass.getName();
        Class<?> externalClass = ClassLoader.getSystemClassLoader().loadClass(className);
        return externalClass;
    }

    public static Class getInternalClass(Class<?> aClass) throws Exception{
        String className = aClass.getName();
        Class<?> internalClass = EXTERNAL_AGENT_CONTEXT.getAgentClassloader().loadClass(className);
        return internalClass;
    }

    public static Field getInternalClassField(Class<?> aClass,String fieldName) throws Exception{
        Class internalClass = getInternalClass(aClass);
        Field declaredField = internalClass.getDeclaredField(fieldName);
        declaredField.setAccessible(true);
        return declaredField;
    }

    public static Method getInternalClassMethod(Class<?> aClass,String methodName, Class<?>... parameterTypes) throws Exception{
        Class internalClass = getInternalClass(aClass);
        Method declaredMethod = internalClass.getDeclaredMethod(methodName,parameterTypes);
        declaredMethod.setAccessible(true);
        return declaredMethod;
    }

    public static Object invokeInternalClassMethod(Object obj, Object[] args,Class<?> aClass,String methodName, Class<?>... parameterTypes) throws Exception{
        Method internalClassMethod = getInternalClassMethod(aClass, methodName, parameterTypes);
        Object invoke = internalClassMethod.invoke(obj,args);
        return invoke;
    }

    public static Object invokeInternalClassStaticMethod(Object[] args,Class<?> aClass,String methodName, Class<?>... parameterTypes) throws Exception{
        Method internalClassMethod = getInternalClassMethod(aClass, methodName, parameterTypes);
        Object invoke = internalClassMethod.invoke(null,args);
        return invoke;
    }

    public static Object execute (String actionTypeValue, Object...args) throws Exception{
        //TODO 上下文基础校验
//        if (com.runtime.pivot.agent.AgentContext.INSTRUMENTATION!=null) {
//        }
//        System.out.println("正在调用execute..........args:"+args);
//        System.out.println("正在调用execute..........args.length:"+args.length);

        Map<String, Method> ACTION_TYPE_METHOD_MAP = EXTERNAL_AGENT_CONTEXT.getActionTypeMethodMap();
        Method method = ACTION_TYPE_METHOD_MAP.get(actionTypeValue);
        AtomicReference<Object> invoke = new AtomicReference<>();
        Runnable runnable = () -> {
            try {
                invoke.set(method.invoke(null, args));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
        invokeInternalClassStaticMethod(new Object[]{actionTypeValue,runnable},AgentConsole.class,"print",String.class, Runnable.class);
        return invoke.get();
    }

}
