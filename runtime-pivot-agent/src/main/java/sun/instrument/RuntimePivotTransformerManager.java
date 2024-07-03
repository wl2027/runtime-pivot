//package sun.instrument;
//
//import java.security.ProtectionDomain;
//
//public class RuntimePivotTransformerManager extends TransformerManager {
//
//    public RuntimePivotTransformerManager(boolean isRetransformable){
//        super(isRetransformable);
//    }
//
//    public static RuntimePivotTransformerManager print() {
//        System.out.println("aaa");
//        return new RuntimePivotTransformerManager(false);
//    }
//
//    @Override
//    public byte[] transform(Module module, ClassLoader loader, String classname, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
//        System.out.println("transform before");
//        byte[] transform = super.transform(module, loader, classname, classBeingRedefined, protectionDomain, classfileBuffer);
//        System.out.println("transform after");
//        return transform;
//    }
//}
