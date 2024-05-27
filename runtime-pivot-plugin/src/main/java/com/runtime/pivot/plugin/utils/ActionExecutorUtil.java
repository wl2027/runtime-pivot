package com.runtime.pivot.plugin.utils;

import cn.hutool.core.util.StrUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

public class ActionExecutorUtil {
    private static final String EXECUTE_EXPRESSION =
            "Class<?> actionExecutorClass = ClassLoader.getSystemClassLoader().loadClass(\"com.runtime.pivot.agent.ActionExecutor\");\n" +
            "Method method = actionExecutorClass.getMethod(\"execute\",String.class,Object[].class);\n" +
            "method.invoke(null,\"{actionType}\",{args});";

    public static String build(String actionType, String... args) {
        StringJoiner joiner = new StringJoiner(",");
        for (String arg : args) {
            joiner.add(arg);
        }
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("actionType",actionType);
        paramMap.put("args",args.length==0?"new Object[]{}":joiner.toString());
        String formatExecuteExpression = StrUtil.format(EXECUTE_EXPRESSION,paramMap);
        return formatExecuteExpression;
    }

    /**
     * Class<?> actionExecutorClass = ClassLoader.getSystemClassLoader().loadClass("com.runtime.pivot.agent.ActionExecutor");
     * Method method = actionExecutorClass.getMethod("execute",String.class,Object[].class);
     * method.invoke(null,"A",a,b,c);
     *
     * Class<?> actionExecutorClass = ClassLoader.getSystemClassLoader().loadClass("com.runtime.pivot.agent.ActionExecutor");
     * Method method = actionExecutorClass.getMethod("execute",String.class,Object[].class);
     * method.invoke(null,"A",new Object[]{});
     * @param args
     */
    public static void main(String[] args) {
        System.out.println(build("A", "a", "b", "c"));
        System.out.println(build("A"));
    }

}
