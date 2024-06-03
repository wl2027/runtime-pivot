//package com.runtime.pivot.plugin.actions.method.test2;
//
//import com.intellij.debugger.engine.DebugProcessImpl;
//import com.intellij.debugger.engine.JavaDebugProcess;
//import com.intellij.debugger.engine.JavaStackFrame;
//import com.intellij.debugger.impl.DebuggerContextImpl;
//import com.intellij.debugger.impl.DebuggerManagerImpl;
//import com.intellij.debugger.ui.breakpoints.BreakpointManager;
//import com.intellij.debugger.ui.breakpoints.Breakpoint;
//import com.intellij.openapi.project.Project;
//import com.intellij.openapi.vfs.VirtualFile;
//import com.intellij.psi.PsiDocumentManager;
//import com.intellij.psi.PsiFile;
//import com.intellij.psi.PsiManager;
//import com.intellij.xdebugger.XDebugSession;
//import com.intellij.xdebugger.frame.XStackFrame;
//import com.intellij.xdebugger.impl.XDebugSessionImpl;
//import com.intellij.xdebugger.impl.breakpoints.XBreakpointManagerImpl;
//import com.intellij.xdebugger.breakpoints.XBreakpoint;
//import com.intellij.xdebugger.breakpoints.XLineBreakpoint;
//import com.intellij.xdebugger.breakpoints.XBreakpointType;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//public class BreakpointFetcher {
//    public static List<XBreakpoint<?>> getBreakpointsFromStackFrame(Project project, XStackFrame stackFrame) {
//        if (!(stackFrame instanceof JavaStackFrame)) {
//            throw new IllegalArgumentException("StackFrame must be an instance of JavaStackFrame");
//        }
//
//        JavaStackFrame javaStackFrame = (JavaStackFrame) stackFrame;
//        DebugProcessImpl debugProcess = ((JavaDebugProcess) javaStackFrame.getDebugProcess()).getDebugProcessImpl();
//        DebuggerContextImpl debuggerContext = debugProcess.getDebuggerContext();
//
//        BreakpointManager breakpointManager = DebuggerManagerImpl.getInstance(project).getBreakpointManager();
//        XDebugSession session = debuggerContext.getDebuggerSession().getXDebugSession();
//
//        if (session == null) {
//            return List.of();
//        }
//
//        XDebugSessionImpl sessionImpl = (XDebugSessionImpl) session;
//        XBreakpointManagerImpl xBreakpointManager = (XBreakpointManagerImpl) sessionImpl.getProject().getComponent(XBreakpointManagerImpl.class);
//
//        PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(stackFrame.getSourcePosition().getFile().getDocument());
//        if (psiFile == null) {
//            return List.of();
//        }
//
//        VirtualFile virtualFile = psiFile.getVirtualFile();
//        int line = stackFrame.getSourcePosition().getLine();
//
//        List<XLineBreakpoint<?>> lineBreakpoints = xBreakpointManager.getDocumentBreakpoints()
//                .stream()
//                .filter(breakpoint -> breakpoint.getFileUrl().equals(virtualFile.getUrl()) && breakpoint.getLine() == line)
//                .collect(Collectors.toList());
//
//        return lineBreakpoints;
//    }
//}
//
