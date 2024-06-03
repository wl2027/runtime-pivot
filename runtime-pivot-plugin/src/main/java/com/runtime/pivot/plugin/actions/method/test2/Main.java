//package com.runtime.pivot.plugin.actions.method.test2;
//
//import com.intellij.xdebugger.breakpoints.XBreakpoint;
//import com.intellij.xdebugger.frame.XStackFrame;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//public class Main {
//    public static void main(String[] args) {
//        XStackFrame stackFrame ;
//        StackFrameUtils.Range<Integer> methodRange = StackFrameUtils.getMethodRange(project, stackFrame);
//        int startLine = methodRange.getStart();
//        int currentLine = stackFrame.getSourcePosition().getLine();
//
//        List<XBreakpoint<?>> relevantBreakpoints = xBreakpointManager.getDocumentBreakpoints()
//                .stream()
//                .filter(breakpoint ->
//                        breakpoint.getFileUrl().equals(virtualFile.getUrl()) &&
//                                breakpoint.getLine() >= startLine &&
//                                breakpoint.getLine() <= currentLine)
//                .collect(Collectors.toList());
//    }
//}
