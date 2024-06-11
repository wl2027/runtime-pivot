package com.runtime.pivot.plugin.domain;

import com.intellij.xdebugger.XSourcePosition;
import com.intellij.xdebugger.breakpoints.XBreakpoint;
import com.intellij.xdebugger.frame.XStackFrame;
import com.runtime.pivot.plugin.enums.BreakpointType;

import java.util.List;

/**
 * Time Travel Debugging
 */
public class BacktrackingXBreakpoint {
    private final XBreakpoint<?> xBreakpoint;
    private final BreakpointType breakpointType;
    private final XStackFrame popXStackFrame;
    private final XStackFrame endXStackFrame;
    //跳跃断点列表
    private final List<XBreakpoint<?>> jumpBreakpointList;
    private final XSourcePosition sourcePosition;

    public BacktrackingXBreakpoint(XBreakpoint<?> xBreakpoint, BreakpointType breakpointType, XStackFrame popXStackFrame, XStackFrame endXStackFrame, List<XBreakpoint<?>> jumpBreakpointList, XSourcePosition sourcePosition) {
        this.xBreakpoint = xBreakpoint;
        this.breakpointType = breakpointType;
        this.popXStackFrame = popXStackFrame;
        this.endXStackFrame = endXStackFrame;
        this.jumpBreakpointList = jumpBreakpointList;
        this.sourcePosition = sourcePosition;
    }

    public XBreakpoint<?> getxBreakpoint() {
        return xBreakpoint;
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
}
