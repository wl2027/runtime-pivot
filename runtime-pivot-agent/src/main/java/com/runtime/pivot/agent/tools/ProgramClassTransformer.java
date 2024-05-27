package com.runtime.pivot.agent.tools;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

//TODO 两个context 拷贝,取的时候取this?卸载呢?当前忽略就可以省略很多问题
public class ProgramClassTransformer implements ClassFileTransformer {
    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        //1.记录loader和className
        return classfileBuffer;
    }
}
