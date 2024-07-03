//package sun.instrument;
//
//import cn.hutool.core.util.ReflectUtil;
//
//import java.lang.instrument.ClassFileTransformer;
//import java.security.ProtectionDomain;
//
//public class RuntimePivotTransformerManagerTest extends TransformerManager {
//
//    private TransformerManager transformerManager;
//
//    public RuntimePivotTransformerManagerTest(TransformerManager transformerManager, boolean isRetransformable){
//        super(isRetransformable);
//        transformerManager = transformerManager;
//    }
//
//    @Override
//    boolean isRetransformable() {
//        return transformerManager.isRetransformable();
//    }
//
//    @Override
//    public synchronized void addTransformer(ClassFileTransformer transformer) {
//        transformerManager.addTransformer(transformer);
//    }
//
//    @Override
//    public synchronized boolean removeTransformer(ClassFileTransformer transformer) {
//        return transformerManager.removeTransformer(transformer);
//    }
//
//    @Override
//    synchronized boolean includesTransformer(ClassFileTransformer transformer) {
//        return transformerManager.includesTransformer(transformer);
//    }
//
//    @Override
//    public byte[] transform(Module module, ClassLoader loader, String classname, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
//        return transformerManager.transform(module, loader, classname, classBeingRedefined, protectionDomain, classfileBuffer);
//    }
//
//    @Override
//    int getTransformerCount() {
//        return transformerManager.getTransformerCount();
//    }
//
//    @Override
//    boolean setNativeMethodPrefix(ClassFileTransformer transformer, String prefix) {
//        return transformerManager.setNativeMethodPrefix(transformer, prefix);
//    }
//
//    @Override
//    String[] getNativeMethodPrefixes() {
//        return transformerManager.getNativeMethodPrefixes();
//    }
//
//    @Override
//    public int hashCode() {
//        return transformerManager.hashCode();
//    }
//
//    @Override
//    public boolean equals(Object obj) {
//        return transformerManager.equals(obj);
//    }
//
//    @Override
//    protected Object clone() throws CloneNotSupportedException {
//        return ReflectUtil.invoke(transformerManager,"clone");
//    }
//
//    @Override
//    public String toString() {
//        return transformerManager.toString();
//    }
//
//    @Override
//    protected void finalize() throws Throwable {
//        ReflectUtil.invoke(transformerManager,"finalize");
//    }
//}
