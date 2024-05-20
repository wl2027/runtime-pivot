package com.runtime.pivot.agent.tools;


import cn.hutool.core.util.ReflectUtil;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import sun.instrument.TransformerManager;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

public class PreAgent {
    public volatile static Instrumentation INST ;
    public volatile static Object[] mTransformerList;
    public volatile static Object[] mTransformerList2;

    public static final String banner =
            "__________ ____ _____________________.___   _____  ___________        __________._______   _______________________\n" +
            "\\______   \\    |   \\      \\__    ___/|   | /     \\ \\_   _____/        \\______   \\   \\   \\ /   /\\_____  \\__    ___/\n" +
            " |       _/    |   /   |   \\|    |   |   |/  \\ /  \\ |    __)_   ______ |     ___/   |\\   Y   /  /   |   \\|    |   \n" +
            " |    |   \\    |  /    |    \\    |   |   /    Y    \\|        \\ /_____/ |    |   |   | \\     /  /    |    \\    |   \n" +
            " |____|_  /______/\\____|__  /____|   |___\\____|__  /_______  /         |____|   |___|  \\___/   \\_______  /____|   \n" +
            "        \\/                \\/                     \\/        \\/                                          \\/         \n";
    public static final String version =" :: Runtime Pivot ::       (v1.0.0.RELEASE)";

    public static void print(){
        try {
            //
            TransformerManager mTransformerManager = (TransformerManager) ReflectUtil.getFieldValue(INST, "mTransformerManager");
            TransformerManager mRetransfomableTransformerManager = (TransformerManager) ReflectUtil.getFieldValue(INST, "mRetransfomableTransformerManager");

            mTransformerList = (Object[]) ReflectUtil.getFieldValue(mTransformerManager, "mTransformerList");
            mTransformerList2 = (Object[]) ReflectUtil.getFieldValue(mRetransfomableTransformerManager, "mTransformerList");
            for (Object o : mTransformerList) {
                ClassFileTransformer mTransformer = (ClassFileTransformer) ReflectUtil.getFieldValue(o, "mTransformer");
                System.out.println(mTransformer.toString()+mTransformer.getClass());
            }
            for (Object o : mTransformerList2) {
                ClassFileTransformer mTransformer = (ClassFileTransformer) ReflectUtil.getFieldValue(o, "mTransformer");
                System.out.println(mTransformer.toString()+mTransformer.getClass());
            }
        }catch (Exception e){
            System.out.println("出现异常:"+e);
        }

    }

    //JVM 首先尝试在代理类上调用以下方法
    public static void premain(String agentArgs, Instrumentation inst) {
        System.out.println(banner);
        System.out.println(version);
        INST=inst;
        inst.addTransformer(new ClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                                    ProtectionDomain protectionDomain, byte[] classfileBuffer) {
                if (className != null && className.equals("com/mysql/jdbc/PreparedStatement")) {
                    System.out.println(">>>>>>>>>>>>>>>>>匹配成功");
                    try {
                        ClassPool pool = ClassPool.getDefault();
                        CtClass ctClass = pool.get("com.mysql.jdbc.PreparedStatement");

                        // Intercept the `execute` method
                        CtMethod executeMethod = ctClass.getDeclaredMethod("execute");
                        executeMethod.insertBefore("{ System.out.println(\"Executing SQL: \" + this.asSql()); }");

                        // Intercept the `executeQuery` method
                        CtMethod executeQueryMethod = ctClass.getDeclaredMethod("executeQuery");
                        executeQueryMethod.insertBefore("{ System.out.println(\"Executing SQL: \" + this.asSql()); }");

                        // Intercept the `executeUpdate` method
                        CtMethod executeUpdateMethod = ctClass.getDeclaredMethod("executeUpdate");
                        executeUpdateMethod.insertBefore("{ System.out.println(\"Executing SQL: \" + this.asSql()); }");

                        return ctClass.toBytecode();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }
        });

        //inst.getAllLoadedClasses()
//        TransformerManager
        /**
         * private final     TransformerManager      mTransformerManager;
         *     private           TransformerManager      mRetransfomableTransformerManager;
         */
        AgentBuilder.Transformer transformer = (builder, typeDescription, classLoader, javaModule, protectionDomain) -> {
            return builder
                    .method(ElementMatchers.named("executeInternal")) // 拦截任意方法
                    .intercept(MethodDelegation.to(MonitorMethod.class)); // 委托
        };

//        AgentBuilder.Transformer transformer2 = (builder, typeDescription, classLoader, javaModule, protectionDomain) -> {
//            return builder
//                    .method(ElementMatchers.named("transform")) // 拦截任意方法
//                    .intercept(MethodDelegation.to(MonitorTransformerManager.class)); // 委托
//        };

        new AgentBuilder
                .Default()
                .type(ElementMatchers.nameStartsWith("com.mysql.jdbc.PreparedStatement"))
                .transform(transformer)
                .installOn(inst);
//        new AgentBuilder
//                .Default()
//                .type(ElementMatchers.nameStartsWith("sun.instrument.TransformerManager"))
//                .transform(transformer2)
//                .installOn(inst);


    }

    //如果代理类没有实现上面的方法，那么 JVM 将尝试调用该方法
    public static void premain(String agentArgs) {
    }

    public static synchronized void setCall(Object call) {
        if (call != null) {
            System.out.println("call有值");
        }
    }

    public static synchronized void setArgs(Object arg) {
        if (arg != null) {
            System.out.println("arg有值");
        }
    }
}
