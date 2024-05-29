package com.runtime.pivot.plugin.utils;

import cn.hutool.core.util.StrUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

public class ActionExecutorUtil {
    public static final String RETURN_OBJECT = "returnObject" ;
    private static final String EXECUTE_EXPRESSION =
            "java.lang.Class<?> actionExecutorClass = java.lang.ClassLoader.getSystemClassLoader().loadClass(\"com.runtime.pivot.agent.ActionExecutor\");\n" +
            "java.lang.reflect.Method method = actionExecutorClass.getMethod(\"execute\",String.class,Object[].class);\n" +
            "Object returnObject = method.invoke(null,\"{actionType}\",new Object[]{{args}});\n" +
            "{script};";

    public static String buildCode(String actionType,String script, String... args) {
        StringJoiner joiner = new StringJoiner(",");
        for (String arg : args) {
            joiner.add(arg);
        }
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("actionType",actionType);
        paramMap.put("args",args.length==0?"":joiner.toString());
        paramMap.put("script",StrUtil.isEmpty(script)?"":script);
        String formatExecuteExpression = StrUtil.format(EXECUTE_EXPRESSION,paramMap);
        return formatExecuteExpression;
    }

    /**
     * <p/>
     * java.lang.Class<?> actionExecutorClass = java.lang.ClassLoader.getSystemClassLoader().loadClass("com.runtime.pivot.agent.ActionExecutor");
     * java.lang.reflect.Method method = actionExecutorClass.getMethod("execute",String.class,Object[].class);
     * Object returnObject = method.invoke(null,"A",new Object[]{a,b,c});
     * ;
     * <p/>
     * java.lang.Class<?> actionExecutorClass = java.lang.ClassLoader.getSystemClassLoader().loadClass("com.runtime.pivot.agent.ActionExecutor");
     * java.lang.reflect.Method method = actionExecutorClass.getMethod("execute",String.class,Object[].class);
     * Object returnObject = method.invoke(null,"A",new Object[]{});
     * ;
     * <p/>
     * java.lang.Class<?> actionExecutorClass = java.lang.ClassLoader.getSystemClassLoader().loadClass("com.runtime.pivot.agent.ActionExecutor");
     * java.lang.reflect.Method method = actionExecutorClass.getMethod("execute",String.class,Object[].class);
     * Object returnObject = method.invoke(null,"A",new Object[]{});
     * ;
     * <p/>
     * new String("./com/wl/1456.json")
     * @param args
     */
    public static void main(String[] args) {
        System.out.println(buildCode("A", null,"a", "b", "c"));
        System.out.println(buildCode("A",null));
        System.out.println(buildCode("A",null));
        System.out.println(buildStringObject("./com/wl/1456.json"));
    }

    public static String buildStringObject(String string) {
        return StrUtil.isEmpty(string)?null:"new String(\""+string+"\")";
    }
}
