package com.runtime.pivot.agent.tools;

import cn.hutool.core.util.ReflectUtil;
import com.runtime.pivot.agent.ActionExecutor;
import sun.instrument.TransformerManager;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.util.Set;

/**
 * @comeFrom arthas
 */
public class InstrumentationUtils {
    /**
     * 无法用retransformClasses,skywalking等字节码增强会触发jvm的bug,
     * 所以只能代理transformerManager
     * addTransformer->重新加载类->transformer->重新定义类->removeTransformer
     * @param inst
     * @param transformer
     * @param classes
     */
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
                    throw new RuntimeException(e);
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
