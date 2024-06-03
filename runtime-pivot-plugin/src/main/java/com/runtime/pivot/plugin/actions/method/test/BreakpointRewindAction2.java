//package com.runtime.pivot.plugin.actions.method.test;
//
//import com.intellij.debugger.DebuggerManagerEx;
//import com.intellij.debugger.engine.DebugProcessImpl;
//import com.intellij.debugger.engine.JavaDebugProcess;
//import com.intellij.debugger.jdi.StackFrameProxyImpl;
//import com.intellij.debugger.ui.breakpoints.Breakpoint;
//import com.intellij.debugger.ui.breakpoints.BreakpointManager;
//import com.intellij.openapi.actionSystem.AnAction;
//import com.intellij.openapi.actionSystem.AnActionEvent;
//import com.intellij.openapi.project.Project;
//import com.intellij.openapi.util.TextRange;
//import com.intellij.psi.PsiFile;
//import com.intellij.psi.PsiManager;
//import com.intellij.xdebugger.XDebugSession;
//
//import java.util.List;
//
//public class BreakpointRewindAction2 extends AnAction {
//    @Override
//    public void actionPerformed(AnActionEvent e) {
//        Project project = e.getProject();
//        if (project == null) return;
//
//        DebuggerManagerEx debuggerManager = DebuggerManagerEx.getInstanceEx(project);
//        JavaDebugProcess debugProcess = (JavaDebugProcess) debuggerManager.getContext().getDebugProcess();
//        if (debugProcess == null) return;
//
//        DebugProcessImpl debugProcessImpl = (DebugProcessImpl) debugProcess.getDebuggerSession().getProcess();
//        try {
//            List<StackFrameProxyImpl> frames = debugProcessImpl.getStackFrames();
//            if (frames == null || frames.isEmpty()) return;
//
//            BreakpointManager breakpointManager = DebuggerManagerEx.getInstanceEx(project).getBreakpointManager();
//            Breakpoint previousBreakpoint = findPreviousBreakpoint(frames, breakpointManager, project);
//            if (previousBreakpoint != null) {
//                // 找到包含断点的栈帧并回溯
//                for (int i = frames.size() - 1; i >= 0; i--) {
//                    StackFrameProxyImpl frame = frames.get(i);
//                    if (frame.equals(previousBreakpoint.getStackFrame())) {
//                        for (int j = 0; j < i; j++) {
//                            StackFrameProxyImpl resetFrame = frames.get(j);
//                            if (debugProcessImpl.canResetFrame(resetFrame)) {
//                                debugProcessImpl.resetFrame(resetFrame);
//                            }
//                        }
//                        break;
//                    }
//                }
//
//                // 恢复执行，自动停止在断点处
//                XDebugSession debugSession = debugProcessImpl.getSession().getXDebugSession();
//                if (debugSession != null) {
//                    debugSession.resume();
//                }
//            }
//        } catch (Exception exception) {
//            exception.printStackTrace();
//        }
//    }
//
//    private Breakpoint findPreviousBreakpoint(List<StackFrameProxyImpl> frames, BreakpointManager breakpointManager, Project project) {
//        try {
//            List<Breakpoint> breakpoints = breakpointManager.getBreakpoints();
//
//            for (StackFrameProxyImpl frame : frames) {
//                String currentFile = frame.location().sourcePath();
//                int currentLine = frame.location().lineNumber();
//                PsiFile currentPsiFile = PsiManager.getInstance(project).findFile(frame.location().sourcePosition().getFile());
//
//                for (Breakpoint breakpoint : breakpoints) {
//                    if (breakpoint.isValid()) {
//                        String breakpointFile = breakpoint.getSourcePosition().getFile().getName();
//                        int breakpointLine = breakpoint.getSourcePosition().getLine();
//
//                        if (breakpointFile.equals(currentFile) && breakpointLine <= currentLine) {
//                            return breakpoint;
//                        } else if (breakpoint.getSourcePosition().getFile().equals(currentPsiFile.getVirtualFile())) {
//                            return breakpoint;
//                        }
//                    }
//                }
//            }
//            return null;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//
//    private boolean isLineBefore(PsiFile file, int line1, int line2) {
//        TextRange range1 = file.getViewProvider().getDocument().getLineRange(line1);
//        TextRange range2 = file.getViewProvider().getDocument().getLineRange(line2);
//        return range1.getStartOffset() < range2.getStartOffset();
//    }
//}
