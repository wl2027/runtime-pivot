package com.runtime.pivot.agent.transformer;

import com.runtime.pivot.agent.ActionExecutor;
import com.runtime.pivot.agent.AgentMain;
import com.runtime.pivot.agent.model.ClassLoadingInfo;
import com.runtime.pivot.agent.tools.StringTool;
import javassist.ClassPool;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 用于classLoadingProcess
 */
public class ClassLoadingTransformer implements ClassFileTransformer {
    @Override
    public byte[] transform(ClassLoader classLoader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        //类加载器并行加载
        if (StringTool.isEmpty(className)) {
            //记录为lambda
            try {
                className = ClassPool.getDefault().makeClass(new ByteArrayInputStream(classfileBuffer)).getName();
            } catch (IOException e) {
                return classfileBuffer;
            }
        }
        //并行加载: classLoadingInfoMap.put(qualifiedName,new LinkedList<>()); LinkedList线程不安全
        String qualifiedName = getQualifiedName(className);
        ClassLoadingInfo classLoadingInfo = new ClassLoadingInfo(classLoader,qualifiedName,className,classBeingRedefined);
        if (ActionExecutor.getAgentContext() == null || ActionExecutor.getAgentContext().getClassLoadingInfoMap()==null) {
            if (AgentMain.classLoadingInfoMap.get(qualifiedName)==null) {
                AgentMain.classLoadingInfoMap.put(qualifiedName, Collections.synchronizedList(new LinkedList<>()));
            }
            List<ClassLoadingInfo> classLoadingInfos = AgentMain.classLoadingInfoMap.get(qualifiedName);
            classLoadingInfos.add(classLoadingInfo);
        }else {
            Map<String, List<ClassLoadingInfo>> classLoadingInfoMap = ActionExecutor.getAgentContext().getClassLoadingInfoMap();
            if (classLoadingInfoMap.get(qualifiedName)==null) {
                classLoadingInfoMap.put(qualifiedName,Collections.synchronizedList(new LinkedList<>()));
            }
            List<ClassLoadingInfo> classLoadingInfos = classLoadingInfoMap.get(qualifiedName);
            classLoadingInfos.add(classLoadingInfo);
        }
        return classfileBuffer;
    }

    private String getQualifiedName(String className) {
        return className.replaceAll("/",".");
    }
}
