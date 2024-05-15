//import com.intellij.openapi.actionSystem.AnAction;
//import com.intellij.openapi.actionSystem.AnActionEvent;
//import com.intellij.openapi.fileChooser.FileChooser;
//import com.intellij.openapi.fileChooser.FileChooserDescriptor;
//import com.intellij.openapi.project.Project;
//import com.intellij.openapi.vfs.VirtualFile;
//import org.benf.cfr.reader.api.CfrDriver;
//import org.benf.cfr.reader.api.OutputSinkFactory;
//import org.benf.cfr.reader.apiunreleased.ClassFileSourceImpl;
//import java.io.*;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//
//public class CFRDecompilerAction extends AnAction {
//
//    @Override
//    public void actionPerformed(AnActionEvent e) {
//        Project project = e.getProject();
//        if (project == null) return;
//
//        // 选择要反编译的 class 文件
//        VirtualFile[] selectedFiles = FileChooser.chooseFiles(new FileChooserDescriptor(true, false, false, false, false, false), project, null);
//        if (selectedFiles.length == 0) return;
//
//        // 获取当前项目的 .idea 文件夹路径
//        String ideaFolderPath = project.getBasePath() + File.separator + ".idea";
//
//        // 反编译每个选定的文件
//        for (VirtualFile file : selectedFiles) {
//            try {
//                byte[] bytecode = Files.readAllBytes(Paths.get(file.getPath()));
//                String decompiledSource = decompile(bytecode);
//
//                // 输出反编译后的源代码
//                System.out.println(decompiledSource);
//
//                // 生成临时文件并删除
//                generateAndDeleteTempFile(ideaFolderPath, decompiledSource);
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
//        }
//    }
//
//    private String decompile(byte[] bytecode) {
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
//
//    private void generateAndDeleteTempFile(String ideaFolderPath, String content) {
//        try {
//            // 创建临时文件
//            File tempFile = new File(ideaFolderPath, "temp.java");
//            FileWriter writer = new FileWriter(tempFile);
//            writer.write(content);
//            writer.close();
//
//            // 在控制台输出临时文件路径
//            System.out.println("Temporary file created: " + tempFile.getAbsolutePath());
//
//            // 在完成后删除临时文件
//            if (tempFile.exists()) {
//                tempFile.delete();
//                System.out.println("Temporary file deleted.");
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//}
