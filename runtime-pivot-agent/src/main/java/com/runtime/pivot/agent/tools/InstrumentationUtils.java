package com.runtime.pivot.agent.tools;

import cn.hutool.core.date.StopWatch;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.util.Collection;
import java.util.Set;

/**
 * 
 * @author hengyunabc 2020-05-25
 *
 */
public class InstrumentationUtils {

    public  static StopWatch stopWatch = new StopWatch();

    public static void start(){
        stopWatch.start();
    }
    public static void stop(){
        stopWatch.stop();
        System.out.println(stopWatch.prettyPrint());
    }

    public static void retransformClasses(Instrumentation inst, ClassFileTransformer transformer,
            Set<Class<?>> classes) {
        try {
            inst.addTransformer(transformer, true);

            for (Class<?> clazz : classes) {
//                if (ClassUtils.isLambdaClass(clazz)) {
////                    logger.info(
////                            "ignore lambda class: {}, because jdk do not support retransform lambda class: https://github.com/alibaba/arthas/issues/1512.",
////                            clazz.getName());
//                    continue;
//                }
                try {
                    inst.retransformClasses(clazz);
                    inst.redefineClasses();
                } catch (Throwable e) {
                    String errorMsg = "retransformClasses class error, name: " + clazz.getName();

                }
            }
        } finally {
            inst.removeTransformer(transformer);
        }
    }

    public static void trigerRetransformClasses(Instrumentation inst, Collection<String> classes) {
        for (Class<?> clazz : inst.getAllLoadedClasses()) {
            if (classes.contains(clazz.getName())) {
                try {
                    inst.retransformClasses(clazz);
                } catch (Throwable e) {
                    String errorMsg = "retransformClasses class error, name: " + clazz.getName();
                }
            }
        }
    }
}
