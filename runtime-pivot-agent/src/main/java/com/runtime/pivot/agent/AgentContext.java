package com.runtime.pivot.agent;

import com.runtime.pivot.agent.model.AgentClassLoader;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;
import java.util.Map;

public class AgentContext {
    public volatile static AgentClassLoader AGENT_CLASSLOADER ;
    public volatile static Instrumentation INSTRUMENTATION ;
    public volatile static Map<String, Method> ACTION_TYPE_METHOD_MAP;
}
