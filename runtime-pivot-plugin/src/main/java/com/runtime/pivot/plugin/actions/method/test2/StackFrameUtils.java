package com.runtime.pivot.plugin.actions.method.test2;

import com.intellij.debugger.engine.JavaStackFrame;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.xdebugger.XSourcePosition;
import com.intellij.xdebugger.frame.XStackFrame;
import com.runtime.pivot.plugin.domain.MethodAnchoring;
import com.runtime.pivot.plugin.domain.MethodBacktrackingContext;
import com.runtime.pivot.plugin.listeners.StackFrameChangedListener;

import java.util.Queue;

public class StackFrameUtils {

//    public static Range<Integer> getMethodRange(Project project, XStackFrame stackFrame) {
//
//    }

    private static PsiMethod getParentMethod(PsiElement element) {
        while (element != null && !(element instanceof PsiMethod)) {
            element = element.getParent();
        }
        return (PsiMethod) element;
    }

    public static void invokeBacktracking(MethodBacktrackingContext methodBacktrackingContext) {
        invokeListener(methodBacktrackingContext);
        //执行一个pop操作
        methodBacktrackingContext.popFrameCommonRunnable();
        //invokeCommand(methodBacktrackingContext);
    }

    private static void invokeListener(MethodBacktrackingContext methodBacktrackingContext) {
        StackFrameChangedListener stackFrameChangedListener = new StackFrameChangedListener(
                methodBacktrackingContext.getxStackFrameRunnableMap(),
                methodBacktrackingContext.getxDebugSession(),
                methodBacktrackingContext.getEndXStackFrame()
        );
        methodBacktrackingContext.getxDebugSession().addSessionListener(stackFrameChangedListener);
        //TODO 先执行一个再往后加监听器执行
    }

    /**
     * TODO 需要编排异步执行任务链
     * @param methodBacktrackingContext
     */
    private static void invokeCommand(MethodBacktrackingContext methodBacktrackingContext) {
        Queue<String> commandQueue = methodBacktrackingContext.getCommandQueue();
        while (!commandQueue.isEmpty()) {
            String command = commandQueue.poll();
            switch (command){
                case MethodBacktrackingContext.popFrame :

                    break;
                case MethodBacktrackingContext.resume:

                    break;
            }
        }
    }

    public static MethodAnchoring getMethodAnchoring(XStackFrame xStackFrame, Project project) {
        if (!(xStackFrame instanceof JavaStackFrame)) {
            throw new IllegalArgumentException("StackFrame must be an instance of JavaStackFrame");
        }

        JavaStackFrame javaStackFrame = (JavaStackFrame) xStackFrame;
        XSourcePosition sourcePosition = javaStackFrame.getSourcePosition();
        if (sourcePosition == null) {
            throw new IllegalArgumentException("SourcePosition is null");
        }

        VirtualFile virtualFile = sourcePosition.getFile();
        PsiFile psiFile = PsiManager.getInstance(project).findFile(virtualFile);
        if (!(psiFile instanceof PsiJavaFile)) {
            throw new IllegalArgumentException("PsiFile must be an instance of PsiJavaFile");
        }

        PsiElement element = psiFile.findElementAt(sourcePosition.getOffset());
        PsiMethod method = getParentMethod(element);
        if (method == null) {
            throw new IllegalArgumentException("Method not found at the given source position");
        }

        PsiCodeBlock methodBody = method.getBody();
        if (methodBody == null) {
            throw new IllegalArgumentException("Method body is null");
        }

        Document document = PsiDocumentManager.getInstance(project).getDocument(psiFile);
        if (document == null) {
            throw new IllegalArgumentException("Document is null");
        }

        int startLine = document.getLineNumber(methodBody.getTextRange().getStartOffset());
        int endLine = document.getLineNumber(methodBody.getTextRange().getEndOffset());

        return new MethodAnchoring(virtualFile,method,startLine, endLine,xStackFrame.getSourcePosition().getLine());
    }

//    public static class Range<T> {
//        private final T start;
//        private final T end;
//
//        private final VirtualFile virtualFile;
//
//        public Range(VirtualFile virtualFile,T start, T end) {
//            this.virtualFile = virtualFile;
//            this.start = start;
//            this.end = end;
//        }
//
//        public T getStart() {
//            return start;
//        }
//
//        public T getEnd() {
//            return end;
//        }
//
//        public VirtualFile getVirtualFile() {
//            return virtualFile;
//        }
//
//        @Override
//        public String toString() {
//            return "Range{" +
//                    "start=" + start +
//                    ", end=" + end +
//                    ", virtualFile=" + virtualFile +
//                    '}';
//        }
//    }
}
