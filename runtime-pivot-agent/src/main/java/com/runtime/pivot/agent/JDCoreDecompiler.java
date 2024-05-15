//package com.runtime.pivot.agent;
//
//import org.jd.core.v1.api.loader.Loader;
//import org.jd.core.v1.api.rewriter.Rewriter;
//import org.jd.core.v1.api.rewriter.RewriterBuilder;
//import org.jd.core.v1.api.writer.Printer;
//import org.jd.core.v1.model.classfile.ClassFile;
//import org.jd.core.v1.model.decompiled.DecompiledClass;
//import org.jd.core.v1.service.decompiler.Decompiler;
//import org.jd.core.v1.service.rewriter.RewriterProviderImpl;
//
//public class JDCoreDecompiler {
//
//    public static String decompile(byte[] classfileBuffer) {
//        // 创建 JD-Core 的 Loader
//        Loader loader = new Loader() {
//            @Override
//            public ClassFile load(String internalName) throws Exception {
//                throw new UnsupportedOperationException();
//            }
//        };
//
//        // 创建 JD-Core 的 Decompiler
//        Decompiler decompiler = new Decompiler(loader);
//
//        // 创建 JD-Core 的 RewriterProvider
//        RewriterProviderImpl rewriterProvider = new RewriterProviderImpl();
//
//        // 创建 JD-Core 的 Rewriter
//        Rewriter rewriter = new RewriterBuilder().build(rewriterProvider.getRewriters());
//
//        // 反编译字节数组中的类文件
//        DecompiledClass decompiledClass = decompiler.decompile(classfileBuffer);
//
//        // 获取反编译后的 Java 源代码字符串
//        Printer printer = new Printer();
//        decompiledClass.accept(printer);
//
//        return printer.toString();
//    }
//
//    public static void main(String[] args) {
//        // 示例用法
//        byte[] classfileBuffer = ...; // 从文件或其他地方获取字节数组
//        String decompiledSource = decompile(classfileBuffer);
//        System.out.println(decompiledSource);
//    }
//}
//
