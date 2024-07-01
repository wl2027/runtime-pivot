//package com.runtime.pivot.agent.transformer;
//
//import javassist.ClassPool;
//import javassist.CtClass;
//import javassist.CtMethod;
//
//import java.lang.instrument.ClassFileTransformer;
//import java.security.ProtectionDomain;
//
//public class JdbcTransformer implements ClassFileTransformer {
//    @Override
//    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
//                            ProtectionDomain protectionDomain, byte[] classfileBuffer) {
//        if (className != null && className.equals("com/mysql/jdbc/PreparedStatement")) {
//            try {
//                ClassPool pool = ClassPool.getDefault();
//                CtClass ctClass = pool.get("com.mysql.jdbc.PreparedStatement");
//
//                // Intercept the `execute` method
//                CtMethod executeMethod = ctClass.getDeclaredMethod("execute");
//                executeMethod.insertBefore("{ System.out.println(\"Executing SQL: \" + this.asSql()); }");
//
//                // Intercept the `executeQuery` method
//                CtMethod executeQueryMethod = ctClass.getDeclaredMethod("executeQuery");
//                executeQueryMethod.insertBefore("{ System.out.println(\"Executing SQL: \" + this.asSql()); }");
//
//                // Intercept the `executeUpdate` method
//                CtMethod executeUpdateMethod = ctClass.getDeclaredMethod("executeUpdate");
//                executeUpdateMethod.insertBefore("{ System.out.println(\"Executing SQL: \" + this.asSql()); }");
//
//                return ctClass.toBytecode();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        return null;
//    }
//}