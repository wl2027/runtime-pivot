package com.runtime.pivot.plugin.model;

import com.intellij.icons.AllIcons;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XSourcePosition;
import com.intellij.xdebugger.breakpoints.XBreakpoint;
import com.intellij.xdebugger.impl.breakpoints.XBreakpointUtil;
import com.runtime.pivot.plugin.enums.XStackBreakpointType;

import javax.swing.*;

/**
 * Time Travel Debugging
 */
public class XStackBreakpoint {
    private XDebugSession myXDebugSession;
    private XBreakpoint<?> myXBreakpoint;
    private Icon myIcon;
    private XStackBreakpointType myXStackBreakpointType;
    private XSourcePosition myXSourcePosition;

    public XStackBreakpoint(XDebugSession xDebugSession,
                            XBreakpoint<?> xBreakpoint,
                            XStackBreakpointType xStackBreakpointType) {
        this.myXDebugSession = xDebugSession;
        this.myXBreakpoint = xBreakpoint;
        this.myXStackBreakpointType = xStackBreakpointType;
        this.myXSourcePosition = xBreakpoint.getSourcePosition();
        buildIcon(xStackBreakpointType);
    }

    private void buildIcon(XStackBreakpointType XStackBreakpointType) {
        switch (XStackBreakpointType) {
            case AVAILABLE:
                this.myIcon = AllIcons.Debugger.Db_set_breakpoint;
                break;
            case UNAVAILABLE:
                this.myIcon = AllIcons.Debugger.Db_muted_breakpoint;
                break;
            case USED:
                this.myIcon = AllIcons.Debugger.Db_verified_breakpoint;
                break;
        }
    }

    public void updateType(){
        if (this.myIcon!=null) {
            if (!myXBreakpoint.isEnabled()) {
                this.myXStackBreakpointType = XStackBreakpointType.DISABLE;
            }
            buildIcon(myXStackBreakpointType);
        }
    }

    @Override
    public String toString() {
        return XBreakpointUtil.getShortText(myXBreakpoint);
    }

    public XDebugSession getXDebugSession() {
        return myXDebugSession;
    }

    public XBreakpoint<?> getXBreakpoint() {
        return myXBreakpoint;
    }

    public Icon getIcon() {
        return myIcon;
    }

    public XStackBreakpointType getXStackBreakpointType() {
        return myXStackBreakpointType;
    }

    public XSourcePosition getXSourcePosition() {
        return myXSourcePosition;
    }
}
