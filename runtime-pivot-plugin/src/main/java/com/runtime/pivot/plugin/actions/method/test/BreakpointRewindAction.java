//package com.example.breakpointrewind;
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
//import com.intellij.openapi.vfs.LocalFileSystem;
//import com.intellij.openapi.vfs.VirtualFile;
//import com.intellij.psi.PsiFile;
//import com.intellij.psi.PsiManager;
//import com.intellij.psi.PsiDocumentManager;
//import com.intellij.openapi.editor.Document;
//import com.intellij.xdebugger.XDebugSession;
//
//import java.util.List;
//
//public class BreakpointRewindAction extends AnAction {
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
//                StackFrameProxyImpl currentFrame = debugProcessImpl.getCurrentFrame();
//                if (currentFrame == null) return;
//
//                boolean resetFrameDone = false;
//
//                // 检查当前栈帧中是否有上一个断点
//                if (previousBreakpoint.getSourcePosition().getLine() + 1 < currentFrame.location().lineNumber()) {
//                    // 在同一方法中找到上一个断点，直接 resetFrame 并 resume
//                    if (debugProcessImpl.canResetFrame(currentFrame)) {
//                        debugProcessImpl.resetFrame(currentFrame);
//                        resetFrameDone = true;
//                    }
//                }
//
//                if (!resetFrameDone) {
//                    // 如果上一个断点不在当前栈帧中，回溯到包含上一个断点的栈帧
//                    for (int i = frames.size() - 1; i >= 0; i--) {
//                        StackFrameProxyImpl frame = frames.get(i);
//                        String frameFile = frame.location().sourceName();
//                        int frameLine = frame.location().lineNumber();
//
//                        if (frameFile.equals(previousBreakpoint.getSourcePosition().getFile().getName())
//                                && frameLine == previousBreakpoint.getSourcePosition().getLine() + 1) {
//                            for (int j = 0; j < i; j++) {
//                                StackFrameProxyImpl resetFrame = frames.get(j);
//                                if (debugProcessImpl.canResetFrame(resetFrame)) {
//                                    debugProcessImpl.resetFrame(resetFrame);
//                                }
//                            }
//                            break;
//                        }
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
//                String currentFile = frame.location().sourceName();
//                int currentLine = frame.location().lineNumber();
//                VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByPath(frame.location().sourcePath());
//                PsiFile currentPsiFile = PsiManager.getInstance(project).findFile(virtualFile);
//
//                for (Breakpoint breakpoint : breakpoints) {
//                    if (breakpoint.isValid()) {
//                        String breakpointFile = breakpoint.getSourcePosition().getFile().getName();
//                        int breakpointLine = breakpoint.getSourcePosition().getLine() + 1; // SourcePosition uses 0-based line numbers
//
//                        if (breakpointFile.equals(currentFile) && breakpointLine < currentLine) {
//                            return breakpoint;
//                        } else if (breakpoint.getSourcePosition().getFile().equals(currentPsiFile.getVirtualFile())) {
//                            if (breakpointLine < currentLine) {
//                                return breakpoint;
//                            }
//                        }
//                    }
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    private boolean isLineBefore(PsiFile file, int line1, int line2) {
//        Document document = PsiDocumentManager.getInstance(file.getProject()).getDocument(file);
//        if (document == null) {
//            return false;
//        }
//
//        int line1StartOffset = document.getLineStartOffset(line1);
//        int line2StartOffset = document.getLineStartOffset(line2);
//        return line1StartOffset < line2StartOffset;
//    }
//}
