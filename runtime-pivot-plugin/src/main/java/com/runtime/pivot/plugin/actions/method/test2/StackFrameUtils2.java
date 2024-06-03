//package com.runtime.pivot.plugin.actions.method.test2;
//import com.intellij.debugger.engine.JavaStackFrame;
//import com.intellij.openapi.editor.Document;
//import com.intellij.openapi.project.Project;
//import com.intellij.openapi.vfs.VirtualFile;
//import com.intellij.psi.*;
//import com.intellij.xdebugger.XBreakpoint;
//import com.intellij.xdebugger.XBreakpointManager;
//import com.intellij.xdebugger.XSourcePosition;
//import com.intellij.xdebugger.breakpoints.XBreakpointType;
//import com.intellij.xdebugger.breakpoints.XLineBreakpoint;
//import com.intellij.xdebugger.breakpoints.XMethodBreakpoint;
//import com.intellij.xdebugger.frame.XStackFrame;
//import com.intellij.xdebugger.impl.XBreakpointManagerImpl;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//public class StackFrameUtils2 {
//
//    public static List<XBreakpoint<?>> getRelevantBreakpoints(Project project, XStackFrame stackFrame, XBreakpointManager breakpointManager) {
//        XSourcePosition sourcePosition = stackFrame.getSourcePosition();
//        if (sourcePosition == null) {
//            throw new IllegalArgumentException("SourcePosition is null");
//        }
//
//        VirtualFile virtualFile = sourcePosition.getFile();
//        PsiFile psiFile = PsiManager.getInstance(project).findFile(virtualFile);
//        if (!(psiFile instanceof PsiJavaFile)) {
//            throw new IllegalArgumentException("PsiFile must be an instance of PsiJavaFile");
//        }
//
//        PsiElement element = psiFile.findElementAt(sourcePosition.getOffset());
//        PsiMethod method = getParentMethod(element);
//        if (method == null) {
//            throw new IllegalArgumentException("Method not found at the given source position");
//        }
//
//        PsiCodeBlock methodBody = method.getBody();
//        if (methodBody == null) {
//            throw new IllegalArgumentException("Method body is null");
//        }
//
//        Document document = PsiDocumentManager.getInstance(project).getDocument(psiFile);
//        if (document == null) {
//            throw new IllegalArgumentException("Document is null");
//        }
//
//        int startLine = document.getLineNumber(methodBody.getTextRange().getStartOffset());
//        int currentLine = sourcePosition.getLine();
//
//        List<XBreakpoint<?>> relevantBreakpoints = breakpointManager.getBreakpoints()
//                .stream()
//                .filter(breakpoint -> isRelevantBreakpoint(breakpoint, virtualFile, startLine, currentLine, method))
//                .collect(Collectors.toList());
//
//        return relevantBreakpoints;
//    }
//
//    private static boolean isRelevantBreakpoint(XBreakpoint<?> breakpoint, VirtualFile virtualFile, int startLine, int currentLine, PsiMethod method) {
//        if (breakpoint instanceof XLineBreakpoint<?>) {
//            XLineBreakpoint<?> lineBreakpoint = (XLineBreakpoint<?>) breakpoint;
//            return lineBreakpoint.getFileUrl().equals(virtualFile.getUrl()) &&
//                    lineBreakpoint.getLine() >= startLine &&
//                    lineBreakpoint.getLine() <= currentLine;
//        } else if (breakpoint instanceof XMethodBreakpoint<?>) {
//            XMethodBreakpoint<?> methodBreakpoint = (XMethodBreakpoint<?>) breakpoint;
//            String methodName = method.getName();
//            PsiClass containingClass = method.getContainingClass();
//            String className = containingClass != null ? containingClass.getQualifiedName() : "";
//            return methodBreakpoint.getMethodName().equals(methodName) &&
//                    methodBreakpoint.getClassName().equals(className);
//        }
//        return false;
//    }
//
//    private static PsiMethod getParentMethod(PsiElement element) {
//        while (element != null && !(element instanceof PsiMethod)) {
//            element = element.getParent();
//        }
//        return (PsiMethod) element;
//    }
//}
