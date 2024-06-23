package com.runtime.pivot.plugin.model;

import com.intellij.debugger.engine.DebugProcessImpl;
import com.intellij.icons.AllIcons;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XSourcePosition;
import com.intellij.xdebugger.breakpoints.XBreakpoint;
import com.intellij.xdebugger.frame.XStackFrame;
import com.intellij.xdebugger.impl.breakpoints.XBreakpointUtil;
import com.runtime.pivot.plugin.enums.RuntimeBreakpointType;

import javax.swing.*;
import java.util.List;

/**
 * Time Travel Debugging
 */
public class BacktrackingXBreakpoint {
    
    private final DebugProcessImpl debugProcess;
    private final XDebugSession xDebugSession;
    private final XBreakpoint<?> xBreakpoint;
    private Icon icon;
    private RuntimeBreakpointType runtimeBreakpointType;
    private final XStackFrame popXStackFrame;
    private final XStackFrame endXStackFrame;
    //跳跃断点列表
    private final List<XBreakpoint<?>> jumpBreakpointList;
    private final XSourcePosition sourcePosition;

    public BacktrackingXBreakpoint(DebugProcessImpl debugProcess, XDebugSession xDebugSession, XBreakpoint<?> xBreakpoint, RuntimeBreakpointType runtimeBreakpointType, XStackFrame popXStackFrame, XStackFrame endXStackFrame, List<XBreakpoint<?>> jumpBreakpointList, XSourcePosition sourcePosition) {
        this.debugProcess = debugProcess;
        this.xDebugSession = xDebugSession;
        this.xBreakpoint = xBreakpoint;
        this.runtimeBreakpointType = runtimeBreakpointType;
        this.popXStackFrame = popXStackFrame;
        this.endXStackFrame = endXStackFrame;
        this.jumpBreakpointList = jumpBreakpointList;
        this.sourcePosition = sourcePosition;
        buildIcon(runtimeBreakpointType);
    }

    private void buildIcon(RuntimeBreakpointType runtimeBreakpointType) {
        switch (runtimeBreakpointType) {
            case AVAILABLE:
                this.icon = AllIcons.Debugger.Db_verified_breakpoint;
                break;
            case NOT_AVAILABLE:
                this.icon = AllIcons.Debugger.Db_muted_breakpoint;
                break;
            default:this.icon = AllIcons.Debugger.Db_muted_breakpoint;
        }
    }

    public void updateType(){
        if (this.xBreakpoint!=null) {
            if (xBreakpoint.isEnabled()) {
                this.runtimeBreakpointType = RuntimeBreakpointType.AVAILABLE;
            } else {
                this.runtimeBreakpointType = RuntimeBreakpointType.NOT_AVAILABLE;
            }
            buildIcon(runtimeBreakpointType);
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

    public RuntimeBreakpointType getBreakpointType() {
        return runtimeBreakpointType;
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
