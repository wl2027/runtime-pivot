//package com.runtime.pivot.plugin.config;
//
//import cn.hutool.core.lang.Editor;
//
//import java.awt.*;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Objects;
//import java.util.concurrent.ConcurrentHashMap;
//import com.intellij.openapi.editor.Document;
//import com.intellij.openapi.editor.impl.DocumentMarkupModel;
//import com.intellij.openapi.editor.markup.MarkupModel;
//import com.intellij.openapi.editor.markup.RangeHighlighter;
//import com.intellij.openapi.editor.markup.TextAttributes;
//import com.intellij.openapi.editor.markup.HighlighterTargetArea;
//import com.intellij.openapi.fileEditor.FileEditorManager;
//import com.intellij.psi.*;
//import com.intellij.psi.util.PsiTreeUtil;
//import com.intellij.ui.JBColor;
//import com.intellij.openapi.project.Project;
//import com.intellij.openapi.vfs.VirtualFile;
//public class RuntimeJvmArgsConfig extends JavaProgramPatcher {
//
//
//}
//
//
//
//
//public class CodeHighlighter {
//    private void scanAndHighlightProjectFiles(Project project) {
//        // 获取 PsiManager
//        PsiManager psiManager = PsiManager.getInstance(project);
//
//        // 获取项目中的所有文件
//        VirtualFile[] virtualFiles = project.getBaseDir().getChildren(); // 获取项目根目录下的所有文件
//        for (VirtualFile virtualFile : virtualFiles) {
//            if (virtualFile.isDirectory()) {
//                continue; // 跳过目录
//            }
//
//            // 获取 PsiFile
//            PsiFile psiFile = psiManager.findFile(virtualFile);
//            if (psiFile != null) {
//                // 执行高亮操作
//                highlightMethodsInFile(project, psiFile);
//            }
//        }
//    }
//    private void highlightMethodsInFile(Project project, PsiFile psiFile) {
//        // 获取 PSI 文件的 Document 对象
//        PsiDocumentManager psiDocumentManager = PsiDocumentManager.getInstance(project);
//        Document document = psiDocumentManager.getDocument(psiFile);
//        if (document == null) {
//            return;
//        }
//
//        // 获取 MarkupModel 对象
//        MarkupModel markupModel = DocumentMarkupModel.forDocument(document, project, true);
//
//        // 遍历所有 PsiMethod
//        PsiMethod[] methods = PsiTreeUtil.getChildrenOfType(psiFile, PsiMethod.class);
//        if (methods != null) {
//            for (PsiMethod method : methods) {
//                // 你可以在这里添加你的条件，筛选出你想要高亮的 method
//                if (shouldHighlight(method)) {
//                    highlightPsiElement(method, markupModel);
//                }
//            }
//        }
//    }
//    public static void scanAndHighlight(Project project) {
//        VirtualFile[] files = FileEditorManager.getInstance(project).getOpenFiles();
//        for (VirtualFile file : files) {
//            PsiFile psiFile = PsiManager.getInstance(project).findFile(file);
//            if (psiFile != null) {
//                CodeHighlighter.highlightMethods(project, psiFile);
//            }
//        }
//    }
//
//    /**
//     * 扫描整个文件中的所有 PsiMethod 并进行高亮
//     *
//     * @param project 当前项目
//     * @param psiFile 目标 PSI 文件
//     */
//    public static void highlightMethods(Project project, PsiFile psiFile) {
//        if (psiFile == null || project == null) {
//            return;
//        }
//
//        // 获取 PSI 文件的 Document 对象
//        PsiDocumentManager psiDocumentManager = PsiDocumentManager.getInstance(project);
//        Document document = psiDocumentManager.getDocument(psiFile);
//        if (document == null) {
//            return;
//        }
//
//        // 获取 MarkupModel 对象
//        MarkupModel markupModel = DocumentMarkupModel.forDocument(document, project, true);
//
//        // 遍历所有 PsiMethod
//        PsiMethod[] methods = PsiTreeUtil.getChildrenOfType(psiFile, PsiMethod.class);
//        if (methods != null) {
//            for (PsiMethod method : methods) {
//                // 你可以在这里添加你的条件，筛选出你想要高亮的 method
//                if (shouldHighlight(method)) {
//                    highlightPsiElement(method, markupModel);
//                }
//            }
//        }
//    }
//
//    /**
//     * 根据某些条件判断是否需要高亮此方法
//     *
//     * @param method PsiMethod
//     * @return 是否高亮
//     */
//    private static boolean shouldHighlight(PsiMethod method) {
//        // 示例：高亮名称包含 "test" 的方法
//        return method.getName().contains("test");
//    }
//
//    /**
//     * 高亮指定的 PsiElement
//     *
//     * @param psiElement 目标 PsiElement
//     * @param markupModel MarkupModel 对象
//     */
//    private static void highlightPsiElement(PsiElement psiElement, MarkupModel markupModel) {
//        // 获取该元素的文本范围
//        int startOffset = psiElement.getTextRange().getStartOffset();
//        int endOffset = psiElement.getTextRange().getEndOffset();
//
//        // 创建高亮样式
//        TextAttributes attributes = new TextAttributes();
//        attributes.setBackgroundColor(JBColor.YELLOW);  // 设置背景色为黄色
//        attributes.setEffectColor(JBColor.RED); // 设置下划线的颜色为红色
//        attributes.setEffectType(TextAttributes.ERASE_MARKER.getEffectType()); // 设置下划线效果
//
//        // 创建 RangeHighlighter 并添加到 MarkupModel
//        RangeHighlighter highlighter = markupModel.addRangeHighlighter(
//                startOffset,  // 高亮的起始位置
//                endOffset,    // 高亮的结束位置
//                0,            // 高亮的优先级
//                attributes,    // 高亮的样式
//                HighlighterTargetArea.EXACT_RANGE  // 高亮的区域
//        );
//    }
//}
//
//package io.wl;
//
//        import com.alibabacloud.intellij.cosy.util.JavaPsiUtils;
//        import com.intellij.execution.Executor;
//        import com.intellij.execution.application.ApplicationConfiguration;
//        import com.intellij.execution.configurations.JavaParameters;
//        import com.intellij.execution.configurations.RunProfile;
//        import com.intellij.execution.runners.JavaProgramPatcher;
//        import com.intellij.ide.todo.TodoConfiguration;
//        import com.intellij.openapi.editor.colors.EditorColorsManager;
//        import com.intellij.openapi.editor.colors.EditorColorsScheme;
//        import com.intellij.openapi.editor.markup.EffectType;
//        import com.intellij.openapi.editor.markup.TextAttributes;
//        import com.intellij.openapi.project.Project;
//        import com.intellij.openapi.project.ProjectManager;
//        import com.intellij.openapi.vfs.VirtualFile;
//        import com.intellij.openapi.vfs.VirtualFileManager;
//        import com.intellij.psi.*;
//        import com.intellij.psi.impl.PsiClassImplUtil;
//        import com.intellij.psi.javadoc.PsiDocToken;
//        import com.intellij.psi.search.GlobalSearchScope;
//        import com.intellij.psi.search.TodoAttributes;
//        import com.intellij.psi.search.TodoAttributesUtil;
//        import com.intellij.psi.search.TodoPattern;
//        import com.intellij.psi.util.PsiClassUtil;
//        import com.intellij.psi.util.PsiTreeUtil;
//        import com.intellij.psi.util.PsiUtil;
//
//        import java.awt.*;
//        import java.util.Arrays;
//        import java.util.List;
//
//public class MyAction extends JavaProgramPatcher {
//
//    //TODO aaa
//    //com.intellij.ide.todo.configurable.PatternDialog
//    @Override
//    public void patchJavaParameters(Executor executor, RunProfile runProfile, JavaParameters javaParameters) {
//        TodoConfiguration instance = TodoConfiguration.getInstance();
//        TodoPattern todo = new TodoPattern(TodoAttributesUtil.createDefault());
//        todo.setCaseSensitive(true);
//        todo.setPatternString("JVM_ARG");
//        TextAttributes textAttributes = todo.getAttributes().getTextAttributes();
////        textAttributes.setForegroundColor(Color.decode(""));
////        textAttributes.setBackgroundColor(Color.decode(""));
////        textAttributes.setErrorStripeColor(Color.decode(""));
////        textAttributes.setEffectColor(Color.decode(""));
//        EditorColorsScheme colorsScheme = EditorColorsManager.getInstance().getGlobalScheme();
//        // 获取当前主题的前景和背景颜色
//        Color foregroundColor = colorsScheme.getDefaultForeground();
//        Color backgroundColor = colorsScheme.getDefaultBackground();
//
//        // 设置前景色（文本颜色）
//        textAttributes.setForegroundColor(foregroundColor != null ? foregroundColor : Color.decode("#000000"));
//
//        // 设置背景色
//        textAttributes.setBackgroundColor(backgroundColor != null ? backgroundColor : Color.decode("#FFFFFF"));
//
//
////        textAttributes.setEffectType(EffectType.BOLD_DOTTED_LINE);
//        List<TodoPattern> todos = List.of(todo);
//        TodoPattern[] todoPatterns = instance.getTodoPatterns();
//        for (TodoPattern todoPattern : todoPatterns) {
//            todos.add(todoPattern);
//        }
//        TextAttributes defaultColorSchemeTextAttributes = TodoAttributesUtil.getDefaultColorSchemeTextAttributes();
//        instance.setTodoPatterns((TodoPattern[])todos.toArray());
//        for (TodoPattern todoPattern : instance.getTodoPatterns()) {
//            System.out.println(todoPattern);
//        }
//        PsiClass psiClass = ((ApplicationConfiguration) runProfile).getMainClass();
//        //PsiClass psiClass = PsiClassFinder.findPsiClassByFqClassName(project, fqClassName);
////        PsiClass psiClass = JavaPsiFacade.getInstance(((ApplicationConfiguration) runProfile).getProject()).findClass(
////                javaParameters.getMainClass(),
////                GlobalSearchScope.allScope(ProjectManager.getInstance().getDefaultProject())
////        );
//        if (psiClass != null) {
//            // 查找该类中的 main 方法
//            PsiMethod mainMethod = findMainMethod(psiClass);
//            if (mainMethod != null) {
//                // 打印 main 方法前的注释
//                printMethodComments(mainMethod);
//            }
//        }
//    }
//    /**
//     * 根据类名查找 PsiClass 对象
//     */
//    private PsiClass findPsiClassByName(Project project, String className) {
////        PsiElementFactory factory = PsiElementFactory.getInstance(project);
////        PsiFile[] psiFiles = PsiManager.getInstance(project).getSearchScope(project).getPsiFiles();
////
////        for (PsiFile psiFile : psiFiles) {
////            if (psiFile instanceof PsiJavaFile) {
////                PsiJavaFile javaFile = (PsiJavaFile) psiFile;
////                for (PsiClass psiClass : javaFile.getClasses()) {
////                    if (psiClass.getName().equals(className)) {
////                        return psiClass;
////                    }
////                }
////            }
////        }
//        return null;
//    }
//
//    /**
//     * 查找类中的 main 方法
//     */
//    private PsiMethod findMainMethod(PsiClass psiClass) {
//        for (PsiMethod method : psiClass.getMethods()) {
//            if (isMainMethod(method)) {
//                return method;
//            }
//        }
//        return null;
//    }
//
//    /**
//     * 判断是否是 main 方法
//     */
//    private boolean isMainMethod(PsiMethod method) {
//        return method.getName().equals("main")
//                && method.getParameterList().getParametersCount() == 1
//                && method.getParameterList().getParameters()[0].getType().getCanonicalText().equals("java.lang.String[]");
//    }
//
//    /**
//     * 打印 main 方法前的注释
//     */
//    private void printMethodComments(PsiMethod method) {
//        //System.out.println(method.getDocComment());
//        Arrays.stream(method.getDocComment().getDescriptionElements()).forEach((element)->{
//            if (element instanceof PsiDocToken){
//                System.out.println(element.getText());
//
//            }
//        });
////        PsiComment[] comments = PsiTreeUtil.collectElementsOfType(method.getContainingFile(), PsiComment.class).toArray(new PsiComment[0]);
////        //method.getDocComment(),有且仅有DocComment会高亮
////        for (PsiComment comment : comments) {
////            if (comment.getTextRange().getEndOffset() < method.getTextRange().getStartOffset()) {
////                System.out.println(comment.getText()); // 打印注释内容
////            }
////        }
//    }
//}
//package io.wl;
//
//        import com.intellij.openapi.editor.Editor;
//        import com.intellij.openapi.editor.markup.MarkupModel;
//        import com.intellij.openapi.editor.markup.RangeHighlighter;
//        import com.intellij.openapi.editor.markup.TextAttributes;
//        import com.intellij.openapi.editor.markup.HighlighterTargetArea;
//        import com.intellij.psi.PsiMethod;
//        import com.intellij.ui.JBColor;
//
//public class PsiElementHighlighter {
//
//    /**
//     * 高亮指定的 PsiElement
//     *
//     * @param editor 当前的编辑器
//     * @param psiElement 要高亮的 PsiElement
//     */
//    public static void highlightPsiElement(Editor editor, com.intellij.psi.PsiElement psiElement) {
//        if (editor == null || psiElement == null) {
//            return;
//        }
//
//        // 获取编辑器的 MarkupModel
//        MarkupModel markupModel = editor.getMarkupModel();
//
//        // 获取 PsiElement 的文本范围
//        int startOffset = psiElement.getTextRange().getStartOffset();
//        int endOffset = psiElement.getTextRange().getEndOffset();
//
//        // 创建高亮样式
//        TextAttributes attributes = new TextAttributes();
//        attributes.setBackgroundColor(JBColor.YELLOW);  // 设置背景色为黄色
//        attributes.setEffectColor(JBColor.RED); // 设置下划线的颜色为红色
//        attributes.setEffectType(TextAttributes.ERASE_MARKER.getEffectType()); // 下划线效果
//
//        // 创建 RangeHighlighter 并添加到 MarkupModel
//        RangeHighlighter highlighter = markupModel.addRangeHighlighter(
//                startOffset,  // 高亮的起始位置
//                endOffset,    // 高亮的结束位置
//                0,            // 高亮的优先级，0 表示默认优先级
//                attributes,    // 高亮的样式
//                HighlighterTargetArea.EXACT_RANGE  // 高亮的区域
//        );
//    }
//
//    private boolean isMainMethod(PsiMethod method) {
//        return method.getName().equals("main")
//                && method.getParameterList().getParametersCount() == 1
//                && method.getParameterList().getParameters()[0].getType().getCanonicalText().equals("java.lang.String[]");
//    }
//}package io.wl;
//
//        import com.intellij.codeInsight.daemon.LineMarkerInfo;
//        import com.intellij.codeInsight.daemon.LineMarkerProvider;
//        import com.intellij.icons.AllIcons;
//        import com.intellij.openapi.editor.markup.GutterIconRenderer;
//        import com.intellij.psi.PsiClass;
//        import com.intellij.psi.PsiComment;
//        import com.intellij.psi.PsiElement;
//        import com.intellij.psi.PsiMethod;
//        import com.intellij.psi.javadoc.PsiDocToken;
//        import com.intellij.psi.util.PsiTreeUtil;
//        import org.jetbrains.annotations.NotNull;
//        import org.jetbrains.annotations.Nullable;
//
//        import java.util.Arrays;
//        import java.util.Collection;
//        import java.util.List;
//        import java.util.Map;
//        import java.util.concurrent.ConcurrentHashMap;
//
///**
// * 1.根据类名找方法=>获取注释=>添加 jvm 参数
// * 2.高亮注释,TODO or 类似 markLine 的打开时扫描监听器
// */
//
//public class TestLineMarkerProvider implements LineMarkerProvider {
//
//    public static final Map<PsiMethod,PsiComment> COMMENT_MAP = new ConcurrentHashMap<>();
//    @Override
//    public @Nullable LineMarkerInfo<?> getLineMarkerInfo(@NotNull PsiElement psiElement) {
//        String qualifiedName = PsiTreeUtil.getParentOfType(psiElement, PsiClass.class).getQualifiedName();
//        PsiMethod method = PsiTreeUtil.getParentOfType(psiElement, PsiMethod.class);
//        if (method != null && isMainMethod(method)) {
//            System.out.println(method.getDocComment().getDescriptionElements());
//            Arrays.stream(method.getDocComment().getDescriptionElements()).forEach((element)->{
//                if (element instanceof PsiDocToken ){
//                    System.out.println(element.getText());
//                }
//            });
//            System.out.println(method.getDocComment().getText());
//
//            List<PsiComment> comments = PsiTreeUtil.findChildrenOfType(method, PsiComment.class).stream().toList();
//            for (PsiComment comment : comments) {
//                if (comment.getText().contains("JVM_ARG")) {
//                    COMMENT_MAP.put(method, comment);
//                    return null;
////                            return new LineMarkerInfo<>(
////                                    comment,
////                                    comment.getTextRange(),
////                                    AllIcons.Mac.AppIconOk512,
////                element -> "Navigate To DatabaseView",
////                (e, elt) -> {},
////                GutterIconRenderer.Alignment.RIGHT,
////                ()->"Data-Pivot Navigate Marker"
////        );
//                }
//            }
//        }
//        return null;
//    }
//
//    private boolean isMainMethod(PsiMethod method) {
//        return method.getName().equals("main")
//                && method.getParameterList().getParametersCount() == 1
//                && method.getParameterList().getParameters()[0].getType().getCanonicalText().equals("java.lang.String[]");
//    }
//
////    private static final JaroWinklerSimilarity JARO_WINKLER = new JaroWinklerSimilarity();
////    private static final AccessCountCache<PsiClass,DbTable> CACHE = new AccessCountCache<>(5,true);
////    private static final AccessCountCache<PsiClass,Boolean> NULL_CACHE = new AccessCountCache<>(3,true);
////
////    public static final void clearCache(){
////        CACHE.clear();
////        NULL_CACHE.clear();
////    }
////
////    public static boolean isSimilar(String str1, String str2) {
////        return getSimilar(str1,str2) >= 0.9;
////    }
////
////    public static double getSimilar(String str1, String str2) {
////        double similarity = JARO_WINKLER.apply(preprocess(str1), preprocess(str2));
////        return similarity;
////    }
////
////    public static String preprocess(String str) {
////        // 将字符串转换为小写并去除下划线
////        return str.toLowerCase().replace("_", "");
////    }
////
////    private final Icon AIMING_COLUMN = IconManager.getInstance().getIcon("/icons/aimingColumn.svg", DataPivotLineMarkerProvider.class);
////    private final Icon AIMING_TABLE = IconManager.getInstance().getIcon("/icons/aimingTable.svg", DataPivotLineMarkerProvider.class);
////
////    @Override
////    public void collectSlowLineMarkers(@NotNull List<? extends PsiElement> elements, @NotNull Collection<? super LineMarkerInfo<?>> result) {
////        for (PsiElement element : elements) {
////            if (element instanceof PsiClass) {
////                PsiClass psiClass = (PsiClass) element;
////                DbTable dbTable = getTableInfo(psiClass);
////                if (dbTable != null) {
////                    result.add(createLineMarkerInfo(psiClass,dbTable));
////                    for (PsiField psiField : psiClass.getFields()) {
////                        DbColumn dbColumn = getColumnInfo(dbTable, psiField);
////                        if (dbColumn != null) {
////                            result.add(createLineMarkerInfo(psiField,dbColumn));
////                        }
////                    }
////                }
////
////            }
////        }
////    }
////
////    public static @Nullable DbTable getTableInfo(PsiClass psiClass) {
////        if (NULL_CACHE.get(psiClass)!=null) {
////            return null;
////        }
////        DbTable cacheDbTable = CACHE.get(psiClass);
////        if (cacheDbTable != null) {
////            return cacheDbTable;
////        }
////        List<DbDataSource> dataSources = DbPsiFacade.getInstance(psiClass.getProject()).getDataSources();
////        DasTable maxDasTable  = null;
////        DbDataSource maxDbDataSource  = null;
////        double maxSimilar  = 0;
////        for (DbDataSource dataSource : dataSources) {
////            List<? extends DasTable> list = DasUtil.getTables(dataSource).toList();
////            for (DasTable dasTable : list) {
////                String dasTableName = dasTable.getName();
////                String elementName = psiClass.getName();
////                double similar = getSimilar(dasTableName, elementName);
////                if (similar>=1) {
////                    DbTable dbTable = (DbTable) DbImplUtilCore.findElement(dataSource, dasTable);
////                    CACHE.put(psiClass,dbTable);
////                    return  dbTable;
////                }
////                if (similar>=0.9 && similar>maxSimilar) {
////                    maxSimilar = similar;
////                    maxDbDataSource = dataSource;
////                    maxDasTable = dasTable;
////                }
////            }
////        }
////        if (maxDasTable == null) {
////            NULL_CACHE.put(psiClass,true);
////            return null;
////        } else {
////            DbTable dbTable = (DbTable) DbImplUtilCore.findElement(maxDbDataSource, maxDasTable);
////            CACHE.put(psiClass,dbTable);
////            return dbTable;
////        }
////    }
////
////    public static @Nullable DbColumn getColumnInfo(DbTable dbTable,PsiField psiField) {
////        return getColumnInfo(dbTable, psiField.getName());
////    }
////
////    public @Nullable static DbColumn getColumnInfo(DbTable dbTable, String fieldName) {
////        DasColumn maxDasColumn  = null;
////        double maxSimilar  = 0;
////        DbDataSource dataSource = dbTable.getDataSource();
////        List<? extends DasColumn> dasColumns = DasUtil.getColumns(dbTable).toList();
////        for (DasColumn dasColumn : dasColumns) {
////            String columnName = dasColumn.getName();
////            double similar = getSimilar(columnName, fieldName);
////            if (similar>=1) {
////                DbElement dbElement = DbImplUtilCore.findElement(dataSource, dasColumn);
////                return (DbColumn) dbElement;
////            }
////            if (similar>=0.9 && similar>maxSimilar) {
////                maxSimilar = similar;
////                maxDasColumn = dasColumn;
////            }
////        }
////        if (maxDasColumn == null) {
////            return null;
////        } else {
////            DbElement dbElement = DbImplUtilCore.findElement(dataSource, maxDasColumn);
////            return (DbColumn) dbElement;
////        }
////    }
////
////    private LineMarkerInfo<PsiElement> createLineMarkerInfo(@NotNull PsiElement psiElement, @NotNull DbElement dbElement) {
////        // 获取正确的导航元素，确保是字段定义行
////        PsiElement navigationElement = psiElement;
////        Icon icon = null;
////        if (psiElement instanceof PsiField) {
////            navigationElement = ((PsiField) psiElement).getNameIdentifier();
////            icon = AIMING_COLUMN;
////        }
////        if (psiElement instanceof PsiClass) {
////            navigationElement = ((PsiClass) psiElement).getNameIdentifier();
////            icon = AIMING_TABLE;
////        }
////        return new LineMarkerInfo<>(
////                navigationElement,
////                navigationElement.getTextRange(),
////                icon,
////                element -> "Navigate To DatabaseView",
////                (e, elt) -> DbNavigationUtils.navigateToDatabaseView(dbElement, true),
////                GutterIconRenderer.Alignment.RIGHT,
////                ()->"Data-Pivot Navigate Marker"
////        );
////    }
////
////    @Override
////    public LineMarkerInfo<?> getLineMarkerInfo(@NotNull PsiElement element) {
////        return null;
////    }
////}
////
////class AccessCountCache<K, V> {
////    private final ConcurrentHashMap<K, V> cache = new ConcurrentHashMap<>();
////    private final ConcurrentHashMap<K, AtomicInteger> accessCountMap = new ConcurrentHashMap<>();
////    private final int maxAccessCount;
////    private final boolean turnOnCaching;
////
////    public AccessCountCache(int maxAccessCount,boolean turnOnCaching) {
////        this.maxAccessCount = maxAccessCount;
////        this.turnOnCaching = turnOnCaching;
////    }
////
////    public synchronized void clear(){
////        cache.clear();
////        accessCountMap.clear();
////    }
////
////    public V get(K key) {
////        if (!turnOnCaching) return null;
////        V value = cache.get(key);
////        if (value != null) {
////            accessCountMap.compute(key, (k, count) -> {
////                if (count == null) {
////                    count = new AtomicInteger(0);
////                }
////                if (count.incrementAndGet() >= maxAccessCount) {
////                    cache.remove(key);
////                    return null;
////                }
////                return count;
////            });
////        }
////        return value;
////    }
////
////    public void put(K key, V value) {
////        if (!turnOnCaching) return ;
////        cache.put(key, value);
////        accessCountMap.put(key, new AtomicInteger(0));
////    }
//
//
//}
