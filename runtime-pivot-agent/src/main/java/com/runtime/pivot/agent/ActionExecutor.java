package com.runtime.pivot.agent;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class ActionExecutor {
    public static Object execute (String actionTypeValue,Object...args) throws Exception{
        //TODO 上下文基础校验
        if (AgentContext.INSTRUMENTATION!=null) {
        }
//        System.out.println("正在调用execute..........args:"+args);
//        System.out.println("正在调用execute..........args.length:"+args.length);

        Class<?> aClass = ActionExecutor.class.getClassLoader().loadClass("com.runtime.pivot.agent.AgentContext");
        Field agentClassLoaderField = aClass.getDeclaredField("AGENT_CLASSLOADER");
        agentClassLoaderField.setAccessible(true);
        ClassLoader agentClassloader = (ClassLoader) agentClassLoaderField.get(null);
        Class<?> agentContextClass = agentClassloader.loadClass("com.runtime.pivot.agent.AgentContext");
        Field actionTypeMethodMapField = agentContextClass.getDeclaredField("ACTION_TYPE_METHOD_MAP");
        actionTypeMethodMapField.setAccessible(true);
        Map<String, Method> ACTION_TYPE_METHOD_MAP = (Map<String, Method>) actionTypeMethodMapField.get(null);
        Method method = ACTION_TYPE_METHOD_MAP.get(actionTypeValue);
        AtomicReference<Object> invoke = new AtomicReference<>();
        Class<?> agentConsoleClass = agentClassloader.loadClass("com.runtime.pivot.agent.AgentConsole");
        Method agentConsolePrint = agentConsoleClass.getDeclaredMethod("print", String.class, Runnable.class);
        agentConsolePrint.setAccessible(true);
        Runnable runnable = () -> {
            try {
                invoke.set(method.invoke(null, args));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
        agentConsolePrint.invoke(null,actionTypeValue,runnable);
        return invoke.get();
    }

}
