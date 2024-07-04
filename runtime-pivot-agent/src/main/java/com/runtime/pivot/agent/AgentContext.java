package com.runtime.pivot.agent;

import com.runtime.pivot.agent.model.AgentClassLoader;
import com.runtime.pivot.agent.model.ClassLoadingInfo;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public class AgentContext {
    public volatile AgentClassLoader agentClassloader ;
    public volatile Instrumentation instrumentation ;
    public volatile Map<String, Method> actionTypeMethodMap;
    public volatile Map<String, List<ClassLoadingInfo>> classLoadingInfoMap;

    public Map<String, List<ClassLoadingInfo>> getClassLoadingInfoMap() {
        return classLoadingInfoMap;
    }

    public void setClassLoadingInfoMap(Map<String, List<ClassLoadingInfo>> classLoadingInfoMap) {
        this.classLoadingInfoMap = classLoadingInfoMap;
    }

    public AgentClassLoader getAgentClassloader() {
        return agentClassloader;
    }

    public void setAgentClassloader(AgentClassLoader agentClassloader) {
        this.agentClassloader = agentClassloader;
    }

    public Instrumentation getInstrumentation() {
        return instrumentation;
    }

    public void setInstrumentation(Instrumentation instrumentation) {
        this.instrumentation = instrumentation;
    }

    public Map<String, Method> getActionTypeMethodMap() {
        return actionTypeMethodMap;
    }

    public void setActionTypeMethodMap(Map<String, Method> actionTypeMethodMap) {
        this.actionTypeMethodMap = actionTypeMethodMap;
    }

}
