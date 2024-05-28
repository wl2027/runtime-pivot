package com.runtime.pivot.agent.config;

import com.runtime.pivot.agent.ActionExecutor;
import com.runtime.pivot.agent.AgentMain;
import com.runtime.pivot.agent.model.ClassLoadingInfo;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ClassLoadingTransformer implements ClassFileTransformer {
    @Override
    public byte[] transform(ClassLoader classLoader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        //类加载器并行加载
        synchronized (ClassLoadingTransformer.class){
            ClassLoadingInfo classLoadingInfo = new ClassLoadingInfo(classLoader,className,classBeingRedefined,protectionDomain,classfileBuffer);
            if (ActionExecutor.getAgentContext() == null || ActionExecutor.getAgentContext().getClassLoadingInfoMap()==null) {
                if (AgentMain.classLoadingInfoMap.get(className)==null) {
                    AgentMain.classLoadingInfoMap.put(className,new LinkedList<>());
                }
                List<ClassLoadingInfo> classLoadingInfos = AgentMain.classLoadingInfoMap.get(className);
                classLoadingInfos.add(classLoadingInfo);
            }else {
                Map<String, List<ClassLoadingInfo>> classLoadingInfoMap = ActionExecutor.getAgentContext().getClassLoadingInfoMap();
                if (classLoadingInfoMap.get(className)==null) {
                    classLoadingInfoMap.put(className,new LinkedList<>());
                }
                List<ClassLoadingInfo> classLoadingInfos = classLoadingInfoMap.get(className);
                classLoadingInfos.add(classLoadingInfo);
            }
            return classfileBuffer;
        }
    }
}
