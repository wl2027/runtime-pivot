package com.runtime.pivot.agent.transformer;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ClassDumpTransformer implements ClassFileTransformer {

    private Set<Class<?>> classes;
    private Map<Class<?>, byte[]> classByteMap;

    public ClassDumpTransformer(Set<Class<?>> classes) {
        this.classes = classes;
        this.classByteMap = new ConcurrentHashMap<>();
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classfileBuffer)
            throws IllegalClassFormatException {
        if (classes.contains(classBeingRedefined)) {
            classByteMap.put(classBeingRedefined, classfileBuffer);
        }
        return null;
    }
    public Map<Class<?>, byte[]> getClassByteMap() {
        return classByteMap;
    }

}
