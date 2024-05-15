//import com.strobel.assembler.metadata.TypeDefinition;
//import com.strobel.decompiler.Decompiler;
//import com.strobel.decompiler.PlainTextOutput;
//
//import java.io.*;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//
//public class ProcyonExample {
//
//    public static void main(String[] args) {
//        // 示例字节码数组
//        byte[] bytecode = getBytecode();
//
//        // 临时保存字节码到.class文件
//        Path classFilePath = saveToTempFile(bytecode);
//
//        // 反编译字节码为格式化的Java源代码字符串
//        String decompiledSource = decompile(classFilePath);
//
//        // 输出反编译后的源代码字符串
//        System.out.println(decompiledSource);
//
//        // 删除临时文件
//        deleteTempFile(classFilePath);
//    }
//
//    private static byte[] getBytecode() {
//        // 这里应该是获取你要反编译的类的字节码数组
//        // 这里仅作示例，实际应根据你的需求获取字节码数组
//        return new byte[] { /* 字节码数组 */ };
//    }
//
//    private static Path saveToTempFile(byte[] bytecode) {
//        try {
//            Path tempDir = Files.createTempDirectory("procyon-temp");
//            Path classFilePath = Paths.get(tempDir.toString(), "temp.class");
//            Files.write(classFilePath, bytecode);
//            return classFilePath;
//        } catch (IOException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//
//    private static String decompile(Path classFilePath) {
//        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//             PrintStream printStream = new PrintStream(byteArrayOutputStream)) {
//            // 使用Procyon进行反编译
//            Decompiler.decompile(classFilePath.toString(), new PlainTextOutput(printStream));
//            return byteArrayOutputStream.toString();
//        } catch (IOException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//
//    private static void deleteTempFile(Path filePath) {
//        try {
//            Files.deleteIfExists(filePath);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//}
