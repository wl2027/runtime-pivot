package com.runtime.pivot.plugin.utils;

import com.intellij.debugger.engine.DebugProcessImpl;
import com.intellij.debugger.engine.SuspendContextImpl;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XSourcePosition;
import com.intellij.xdebugger.breakpoints.XBreakpoint;
import com.intellij.xdebugger.frame.XStackFrame;
import com.runtime.pivot.plugin.domain.BacktrackingXBreakpoint;
import com.runtime.pivot.plugin.domain.MethodAnchoring;
import com.runtime.pivot.plugin.domain.MethodBacktrackingContext;
import com.runtime.pivot.plugin.listeners.XStackFrameListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class StackFrameUtils {

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
    }

    public static void invokeBacktracking(BacktrackingXBreakpoint backtrackingXBreakpoint) {
        XStackFrameListener xStackFrameListener = new XStackFrameListener(
                backtrackingXBreakpoint.getxDebugSession(),
                backtrackingXBreakpoint.getEndXStackFrame(),
                backtrackingXBreakpoint.getSourcePosition(),
                ()->{
                    try {
                        resumeCommonRunnable(backtrackingXBreakpoint.getDebugProcess(),backtrackingXBreakpoint.getxDebugSession(),backtrackingXBreakpoint.getJumpBreakpointList());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
        ) {
            @Override
            public void stackFrameExecutionMethod() throws Exception {
                resumeCommonRunnable(backtrackingXBreakpoint.getDebugProcess(),backtrackingXBreakpoint.getxDebugSession(),backtrackingXBreakpoint.getJumpBreakpointList());
            }
        };
        backtrackingXBreakpoint.getxDebugSession().addSessionListener(xStackFrameListener);
        popFrameCommonRunnable(backtrackingXBreakpoint.getxDebugSession(),backtrackingXBreakpoint.getPopXStackFrame());
    }

    public static void resumeCommonRunnable(DebugProcessImpl debugProcess, XDebugSession xDebugSession, List<XBreakpoint<?>> jumpBreakpointList) throws Exception {
        java.util.List<Boolean> stateList = new ArrayList<>() ;
        for (XBreakpoint<?> xBreakpoint : jumpBreakpointList) {
            stateList.add(xBreakpoint.isEnabled());
            xBreakpoint.setEnabled(false);
        }
        DebugProcessImpl.ResumeCommand resumeCommand = debugProcess.createResumeCommand((SuspendContextImpl) xDebugSession.getSuspendContext());
        try {
            resumeCommand.run();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        //jumpBreakpointList 多禁用一个end栈帧的断点就能解决
        for (int i = 0; i < jumpBreakpointList.size(); i++) {
            jumpBreakpointList.get(i).setEnabled(stateList.get(i));
        }
//        DebugProcessImpl.ResumeCommand resumeCommand = debugProcess.createResumeCommand((SuspendContextImpl) xDebugSession.getSuspendContext());
//        resumeCommand.run();
//        ApplicationManager.getApplication().executeOnPooledThread(() -> {
//            try {
//                java.util.List<Boolean> stateList = new ArrayList<>() ;
//                for (XBreakpoint<?> xBreakpoint : jumpBreakpointList) {
//                    stateList.add(xBreakpoint.isEnabled());
//                    xBreakpoint.setEnabled(false);
//                }
//                DebugProcessImpl.ResumeCommand resumeCommand = debugProcess.createResumeCommand((SuspendContextImpl) xDebugSession.getSuspendContext());
//                resumeCommand.run();
//                for (int i = 0; i < jumpBreakpointList.size(); i++) {
//                    jumpBreakpointList.get(i).setEnabled(stateList.get(i));
//                }
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
//        });
    }

    public static void popFrameCommonRunnable(XDebugSession xDebugSession, XStackFrame popXStackFrame) {
        xDebugSession.getDebugProcess().getDropFrameHandler().drop(popXStackFrame);
    }

    private static void invokeListener(MethodBacktrackingContext methodBacktrackingContext) {
        XStackFrameListener xStackFrameListener = new XStackFrameListener(
                methodBacktrackingContext.getxDebugSession(),
                methodBacktrackingContext.getEndXStackFrame(),
                null,
                null
        ) {
            @Override
            public void stackFrameExecutionMethod() {
                methodBacktrackingContext.resumeCommonRunnable();
            }
        };
        methodBacktrackingContext.getxDebugSession().addSessionListener(xStackFrameListener);
    }


    public static MethodAnchoring getMethodAnchoring(XStackFrame xStackFrame, Project project) {
        XSourcePosition sourcePosition = xStackFrame.getSourcePosition();
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
}

