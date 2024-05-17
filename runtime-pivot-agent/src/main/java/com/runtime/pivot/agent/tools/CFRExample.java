//package com.runtime.pivot.agent;
//
//import org.benf.cfr.reader.api.CfrDriver;
//import org.benf.cfr.reader.api.OutputSinkFactory;
//import org.benf.cfr.reader.state.ClassFileSourceImpl;
//
//import java.io.ByteArrayInputStream;
//import java.io.ByteArrayOutputStream;
//
//public class CFRExample {
//
//    public static void main(String[] args) {
//        // 示例字节码数组
//        byte[] bytecode = getBytecode();
//
//        // 反编译字节码为格式化的 Java 源代码字符串
//        String decompiledSource = decompile(bytecode);
//
//        // 输出反编译后的源代码字符串
//        System.out.println(decompiledSource);
//    }
//
//    private static byte[] getBytecode() {
//        // 这里应该是获取你要反编译的类的字节码数组
//        // 这里仅作示例，实际应根据你的需求获取字节码数组
//        return new byte[] { /* 字节码数组 */ };
//    }
//
//    private static String decompile(byte[] bytecode) {
//        try {
//            // 创建字节码输入流
//            ByteArrayInputStream inputStream = new ByteArrayInputStream(bytecode);
//
//            // 创建字节码文件源
//            ClassFileSourceImpl classFileSource = new ClassFileSourceImpl(inputStream);
//
//            // 创建输出流，用于接收反编译后的源代码
//            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//
//            // 创建 CFR 驱动程序
//            CfrDriver cfr = new CfrDriver.Builder()
//                    .withClassFileSource(classFileSource)
//                    .withOutputSink(OutputSinkFactory.sinkToStream(outputStream))
//                    .build();
//
//            // 进行反编译
//            cfr.analyse();
//
//            // 获取反编译后的源代码
//            String decompiledSource = outputStream.toString();
//
//            // 关闭流
//            outputStream.close();
//            inputStream.close();
//
//            return decompiledSource;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//}
