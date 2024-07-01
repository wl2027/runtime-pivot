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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ClassLoadingTransformer implements ClassFileTransformer {
    @Override
    public byte[] transform(ClassLoader classLoader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        //类加载器并行加载
        //TODO className is null E:\002_Code\000_github\IDEA\runtime-pivot\runrun\org\springframework\boot\autoconfigure\condition
        //org.springframework.boot.autoconfigure.condition.OnPropertyCondition$$Lambda$267
        if (StringTool.isEmpty(className)) {
            //记录为lamda
            try {
                String name = new ClassPool().makeClass(new ByteArrayInputStream(classfileBuffer)).getName();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return classfileBuffer;
        }
        //TODO 为什么要加锁? 20240629 AgentMain.classLoadingInfoMap.put(qualifiedName,new LinkedList<>()); LinkedList线程不安全
        //TODO解决方式 ConcurrentLinkedQueue Collections.synchronizedList
//        synchronized (ClassLoadingTransformer.class){
//            String qualifiedName = getQualifiedName(className);
//            ClassLoadingInfo classLoadingInfo = new ClassLoadingInfo(classLoader,qualifiedName,className,classBeingRedefined);
//            if (ActionExecutor.getAgentContext() == null || ActionExecutor.getAgentContext().getClassLoadingInfoMap()==null) {
//                if (AgentMain.classLoadingInfoMap.get(qualifiedName)==null) {
//                    AgentMain.classLoadingInfoMap.put(qualifiedName,new LinkedList<>());
//                }
//                List<ClassLoadingInfo> classLoadingInfos = AgentMain.classLoadingInfoMap.get(qualifiedName);
//                classLoadingInfos.add(classLoadingInfo);
//            }else {
//                Map<String, List<ClassLoadingInfo>> classLoadingInfoMap = ActionExecutor.getAgentContext().getClassLoadingInfoMap();
//                if (classLoadingInfoMap.get(qualifiedName)==null) {
//                    classLoadingInfoMap.put(qualifiedName,new LinkedList<>());
//                }
//                List<ClassLoadingInfo> classLoadingInfos = classLoadingInfoMap.get(qualifiedName);
//                classLoadingInfos.add(classLoadingInfo);
//            }
//            return classfileBuffer;
//        }
        String qualifiedName = getQualifiedName(className);
        ClassLoadingInfo classLoadingInfo = new ClassLoadingInfo(classLoader,qualifiedName,className,classBeingRedefined);
        if (ActionExecutor.getAgentContext() == null || ActionExecutor.getAgentContext().getClassLoadingInfoMap()==null) {
            //先用临时的
            if (AgentMain.classLoadingInfoMap.get(qualifiedName)==null) {
                AgentMain.classLoadingInfoMap.put(qualifiedName,new LinkedList<>());
            }
            List<ClassLoadingInfo> classLoadingInfos = AgentMain.classLoadingInfoMap.get(qualifiedName);
            classLoadingInfos.add(classLoadingInfo);
        }else {
            //用putAll后的
            Map<String, List<ClassLoadingInfo>> classLoadingInfoMap = ActionExecutor.getAgentContext().getClassLoadingInfoMap();
            if (classLoadingInfoMap.get(qualifiedName)==null) {
                classLoadingInfoMap.put(qualifiedName,new LinkedList<>());
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
