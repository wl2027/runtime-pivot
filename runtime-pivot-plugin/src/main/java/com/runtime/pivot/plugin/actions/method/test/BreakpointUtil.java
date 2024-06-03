package com.runtime.pivot.plugin.actions.method.test;

import com.intellij.openapi.project.Project;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebuggerManager;
import com.intellij.xdebugger.XSourcePosition;
import com.intellij.xdebugger.breakpoints.XBreakpoint;
import com.intellij.xdebugger.breakpoints.XBreakpointManager;

public class BreakpointUtil {

    /**
     * 要获取启用的并且执行过的?
     * @param project
     * @return
     */
    public static XBreakpoint<?> getPreviousBreakpoint(Project project) {
        XDebuggerManager debuggerManager = XDebuggerManager.getInstance(project);
        XDebugSession currentSession = debuggerManager.getCurrentSession();

        if (currentSession == null) {
            return null;
        }

        XSourcePosition currentPosition = currentSession.getCurrentPosition();
        if (currentPosition == null) {
            return null;
        }

        XBreakpointManager breakpointManager = debuggerManager.getBreakpointManager();
        XBreakpoint<?>[] breakpoints = breakpointManager.getAllBreakpoints();

        XBreakpoint<?> previousBreakpoint = null;
        for (XBreakpoint<?> breakpoint : breakpoints) {
            XSourcePosition breakpointPosition = breakpoint.getSourcePosition();
            if (breakpointPosition != null && isBefore(breakpointPosition, currentPosition)) {
                if (previousBreakpoint == null || isBefore(previousBreakpoint.getSourcePosition(), breakpointPosition)) {
                    previousBreakpoint = breakpoint;
                }
            }
        }

        return previousBreakpoint;
    }

    private static boolean isBefore(XSourcePosition pos1, XSourcePosition pos2) {
        if (pos1.getFile().equals(pos2.getFile())) {
            return pos1.getLine() < pos2.getLine();
        }
        return false;
    }
}

