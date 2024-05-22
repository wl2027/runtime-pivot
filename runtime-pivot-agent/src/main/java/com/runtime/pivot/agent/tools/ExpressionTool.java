package com.runtime.pivot.agent.tools;

import com.runtime.pivot.agent.config.ActionType;
import com.runtime.pivot.agent.providers.ClassEnhanceProvider;
import com.runtime.pivot.agent.providers.EnhanceProvider;

import java.util.StringJoiner;

public class ExpressionTool {
    public static String executeMethodString(Class eClass, String methodName, String... args) {
        StringBuilder expression = new StringBuilder(eClass.getName());
        expression.append(".").append(methodName).append("(");

        StringJoiner joiner = new StringJoiner(",");
        for (String arg : args) {
            joiner.add(arg);
        }

        expression.append(joiner.toString()).append(")");
        return expression.toString();
    }
    public static String executeProvider (Class<? extends EnhanceProvider> eClass, ActionType actionType, String... args) {
        return executeMethodString(eClass,actionType.toString());
    }
}
