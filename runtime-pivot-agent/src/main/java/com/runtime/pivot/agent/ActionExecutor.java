package com.runtime.pivot.agent;

import cn.hutool.core.exceptions.ExceptionUtil;
import com.runtime.pivot.agent.config.AgentConstants;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class ActionExecutor {
    private static final ThreadLocal<ActionContext> actionContextThreadLocal = new ThreadLocal<>();
    //内部上线文
    private static Object INTERNAL_AGENT_CONTEXT;

    //外部上下文
    private static AgentContext EXTERNAL_AGENT_CONTEXT;

    private static ClassLoader ACTION_CLASS_LOADER;

    public static ActionContext getActionContext(){
        return actionContextThreadLocal.get();
    }

    public static void initActionContext(ActionContext actionContext){
        actionContextThreadLocal.set(actionContext);
    }

    public static void removeActionContext(){
        actionContextThreadLocal.remove();
    }

    public static AgentContext getAgentContext() {
        return EXTERNAL_AGENT_CONTEXT;
    }

    public static void setAgentContext(AgentContext agentContext){
        EXTERNAL_AGENT_CONTEXT=agentContext;
    }

    public static ClassLoader getActionClassLoader() {
        return ACTION_CLASS_LOADER;
    }

    public static void setActionClassLoader(ClassLoader actionClassLoader) {
        ACTION_CLASS_LOADER = actionClassLoader;
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

    //MY 反射调用,会报空指针异常,因为当成args=null,而不是args内元素是null
    public static synchronized Object execute (String actionTypeValue, Object...args) throws Exception{
        //TODO 上下文基础校验
//        if (com.runtime.pivot.agent.AgentContext.INSTRUMENTATION!=null) {
//        }
//        System.out.println("正在调用execute..........args:"+args);
//        System.out.println("正在调用execute..........args.length:"+args.length);

        if (args != null && args.length>0) {
            for (Object arg : args) {
                if (arg!=null) {
                    //MY TOIDO arg.getClass().getClassLoader()可能为null,参数是根加载器加载的时候需要格外的appclassloader,
                    // 不过既然用不到该参数正常来说也不用该参数的classloader
                    ClassLoader classLoader = arg.getClass().getClassLoader();
                    ActionExecutor.setActionClassLoader(classLoader);
                    break;
                }
            }
        }
        //将ActionClassLoader注册为AgentClassloader内的CurrentClassLoader
        getAgentContext().getAgentClassloader().setCurrentClassLoader(getActionClassLoader());
//        if (ActionExecutor.getActionClassLoader() == null) {
//            throw new RuntimePivotException("args is all null");
//        }
        Map<String, Method> ACTION_TYPE_METHOD_MAP = EXTERNAL_AGENT_CONTEXT.getActionTypeMethodMap();
        Method method = ACTION_TYPE_METHOD_MAP.get(actionTypeValue);
        AtomicReference<Object> invoke = new AtomicReference<>();
        Runnable runnable = () -> {
            Object result = null;
            try {
                result = method.invoke(null, args);
            } catch (Exception e) {
                result = "Agent execution error !\nError message : \n"+ ExceptionUtil.stacktraceToString(e,5000);
                System.out.print(AgentConstants.ANSI_BOLD);
                System.err.println(result);
                System.out.print(AgentConstants.RESET);
            } finally {
                invoke.set(String.valueOf(result));
            }
        };
        //调用内部AgentConsole,避免加载内部类
        invokeInternalClassStaticMethod(new Object[]{actionTypeValue,runnable},AgentConsole.class,"print",String.class, Runnable.class);
        return invoke.get();
    }

}
