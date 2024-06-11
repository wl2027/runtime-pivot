package com.runtime.pivot.plugin.domain;

import com.intellij.debugger.engine.DebugProcessImpl;
import com.intellij.icons.AllIcons;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XSourcePosition;
import com.intellij.xdebugger.breakpoints.XBreakpoint;
import com.intellij.xdebugger.frame.XStackFrame;
import com.intellij.xdebugger.impl.breakpoints.XBreakpointUtil;
import com.runtime.pivot.plugin.enums.BreakpointType;
import com.runtime.pivot.plugin.utils.RuntimePivotUtil;

import javax.swing.*;
import java.util.List;

/**
 * Time Travel Debugging
 */
public class BacktrackingXBreakpoint {
    
    private final DebugProcessImpl debugProcess;
    private final XDebugSession xDebugSession;
    private final XBreakpoint<?> xBreakpoint;
    private final Icon icon;
    private final BreakpointType breakpointType;
    private final XStackFrame popXStackFrame;
    private final XStackFrame endXStackFrame;
    //跳跃断点列表
    private final List<XBreakpoint<?>> jumpBreakpointList;
    private final XSourcePosition sourcePosition;

    public BacktrackingXBreakpoint(DebugProcessImpl debugProcess, XDebugSession xDebugSession, XBreakpoint<?> xBreakpoint, BreakpointType breakpointType, XStackFrame popXStackFrame, XStackFrame endXStackFrame, List<XBreakpoint<?>> jumpBreakpointList, XSourcePosition sourcePosition) {
        this.debugProcess = debugProcess;
        this.xDebugSession = xDebugSession;
        this.xBreakpoint = xBreakpoint;
        this.breakpointType = breakpointType;
        this.popXStackFrame = popXStackFrame;
        this.endXStackFrame = endXStackFrame;
        this.jumpBreakpointList = jumpBreakpointList;
        this.sourcePosition = sourcePosition;
        switch (breakpointType) {
            case AVAILABLE:
                this.icon = AllIcons.Debugger.Db_verified_breakpoint;
                break;
            case NOT_AVAILABLE:
                this.icon = AllIcons.Debugger.Db_muted_breakpoint;
                break;
            default:this.icon = AllIcons.Debugger.Db_muted_breakpoint;
        }
    }

    @Override
    public String toString() {
        return XBreakpointUtil.getShortText(xBreakpoint);
    }

    public XBreakpoint<?> getxBreakpoint() {
        return xBreakpoint;
    }

    public Icon getIcon() {
        return icon;
    }

    public BreakpointType getBreakpointType() {
        return breakpointType;
    }

    public XStackFrame getPopXStackFrame() {
        return popXStackFrame;
    }

    public XStackFrame getEndXStackFrame() {
        return endXStackFrame;
    }

    public List<XBreakpoint<?>> getJumpBreakpointList() {
        return jumpBreakpointList;
    }

    public XSourcePosition getSourcePosition() {
        return sourcePosition;
    }

    public DebugProcessImpl getDebugProcess() {
        return debugProcess;
    }

    public XDebugSession getxDebugSession() {
        return xDebugSession;
    }
}
