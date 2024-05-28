package com.runtime.pivot.plugin.utils;

import cn.hutool.core.util.StrUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

public class ActionExecutorUtil {
    private static final String EXECUTE_EXPRESSION =
            "java.lang.Class<?> actionExecutorClass = java.lang.ClassLoader.getSystemClassLoader().loadClass(\"com.runtime.pivot.agent.ActionExecutor\");\n" +
            "java.lang.reflect.Method method = actionExecutorClass.getMethod(\"execute\",String.class,Object[].class);\n" +
            "method.invoke(null,\"{actionType}\",new Object[]{{args}});";

    public static String buildCode(String actionType, String... args) {
        StringJoiner joiner = new StringJoiner(",");
        for (String arg : args) {
            joiner.add(arg);
        }
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("actionType",actionType);
        paramMap.put("args",args.length==0?"":joiner.toString());
        //paramMap.put("returnObject",StrUtil.isEmpty(returnObject)?"":returnObject+" = ");
        String formatExecuteExpression = StrUtil.format(EXECUTE_EXPRESSION,paramMap);
        return formatExecuteExpression;
    }

    /**
     * <p/>
     * java.lang.Class<?> actionExecutorClass = java.lang.ClassLoader.getSystemClassLoader().loadClass("com.runtime.pivot.agent.ActionExecutor");
     * java.lang.reflect.Method method = actionExecutorClass.getMethod("execute",String.class,Object[].class);
     * method.invoke(null,"A",new Object[]{a,b,c});
     * <p/>
     * java.lang.Class<?> actionExecutorClass = java.lang.ClassLoader.getSystemClassLoader().loadClass("com.runtime.pivot.agent.ActionExecutor");
     * java.lang.reflect.Method method = actionExecutorClass.getMethod("execute",String.class,Object[].class);
     * method.invoke(null,"A",new Object[]{});
     * <p/>
     * java.lang.Class<?> actionExecutorClass = java.lang.ClassLoader.getSystemClassLoader().loadClass("com.runtime.pivot.agent.ActionExecutor");
     * java.lang.reflect.Method method = actionExecutorClass.getMethod("execute",String.class,Object[].class);
     * method.invoke(null,"A",new Object[]{});
     * <p/>
     * new String("./com/wl/1456.json")
     * @param args
     */
    public static void main(String[] args) {
        System.out.println(buildCode("A", "a", "b", "c"));
        System.out.println(buildCode("A"));
        System.out.println(buildCode("A"));
        System.out.println(buildStringObject("./com/wl/1456.json"));
    }

    public static String buildStringObject(String path) {
        return "new String(\""+path+"\")";
    }
}
