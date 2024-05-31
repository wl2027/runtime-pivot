package com.runtime.pivot.agent.tools;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.util.Set;

/**
 * @comeFrom arthas
 */
public class InstrumentationUtils {
    public static void retransformClasses(Instrumentation inst, ClassFileTransformer transformer,
            Set<Class<?>> classes) {
        try {
            inst.addTransformer(transformer, true);
            for (Class<?> clazz : classes) {
                if (false && isLambdaClass(clazz)) {
                    //lambda
                    System.out.println("ignore lambda class: {}, because jdk do not support retransform lambda class: https://github.com/alibaba/arthas/issues/1512."+clazz.getName());
                    continue;
                }
                try {
                    //重新加载类
                    inst.retransformClasses(clazz);
                    //重新定义类
                    inst.redefineClasses();
                } catch (Throwable e) {
                    String errorMsg = "retransform Classes class error, name: " + clazz.getName();
                    System.out.println(errorMsg);
                    e.printStackTrace();
                }
            }
        } finally {
            inst.removeTransformer(transformer);
        }
    }

    public static boolean isLambdaClass(Class<?> clazz) {
        return clazz.getName().contains("$$Lambda$");
    }
}
