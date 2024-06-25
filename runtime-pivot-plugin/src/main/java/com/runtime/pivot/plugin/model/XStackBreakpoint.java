package com.runtime.pivot.plugin.model;

import com.intellij.icons.AllIcons;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XSourcePosition;
import com.intellij.xdebugger.breakpoints.XBreakpoint;
import com.intellij.xdebugger.frame.XStackFrame;
import com.intellij.xdebugger.impl.breakpoints.XBreakpointUtil;
import com.runtime.pivot.plugin.enums.XStackBreakpointType;

import javax.swing.*;

/**
 * Time Travel Debugging
 */
public class XStackBreakpoint {
    private boolean isBottomCurrentXStackFrame;
    //栈底: xStackFramePosition = 1
    private int xStackFramePosition;
    private XStackFrame xStackFrame;
    private XStackFrame currentXStackFrame;
    private XStackFrame bottomXStackFrame;
    private XStackFrameMethod xStackFrameMethod;
    private XBreakpoint<?> myXBreakpoint;
    private Icon myIcon;
    private XStackBreakpointType myXStackBreakpointType;
    private XSourcePosition myXSourcePosition;

    public XStackBreakpoint(boolean isBottomCurrentXStackFrame,
                            int xStackFramePosition,
                            XStackFrame xStackFrame,
                            XStackFrame currentXStackFrame,
                            XStackFrame bottomXStackFrame,
                            XStackFrameMethod xStackFrameMethod,
                            XBreakpoint<?> xBreakpoint) {
        this.isBottomCurrentXStackFrame = isBottomCurrentXStackFrame;
        this.xStackFramePosition = xStackFramePosition;
        this.xStackFrame = xStackFrame;
        this.currentXStackFrame = currentXStackFrame;
        this.bottomXStackFrame = bottomXStackFrame;
        this.xStackFrameMethod = xStackFrameMethod;
        this.myXBreakpoint = xBreakpoint;
        this.myXStackBreakpointType = accessibilityAnalysis();
        this.myXSourcePosition = xBreakpoint.getSourcePosition();
        buildIcon(this.myXStackBreakpointType);
    }

    private XStackBreakpointType accessibilityAnalysis() {
        XStackBreakpointType xStackBreakpointType = null;
        //是否在当前栈帧的底部
        if (isBottomCurrentXStackFrame) {
            //下
            xStackBreakpointType =  XStackBreakpointType.USED;
        }else {
            //上
            xStackBreakpointType = XStackBreakpointType.AVAILABLE;
        }
        //是否是底部栈帧
        if (xStackFramePosition==1 || bottomXStackFrame == null){
            xStackBreakpointType = XStackBreakpointType.UNAVAILABLE;
        }
        //断点是否启用
        if (!myXBreakpoint.isEnabled()){
            xStackBreakpointType = XStackBreakpointType.DISABLE;
        }
        //断点位置是否为当前调用位置
        if (myXBreakpoint.getSourcePosition().getFile().getUrl().equals(currentXStackFrame.getSourcePosition().getFile().getUrl())
                && myXBreakpoint.getSourcePosition().getLine()==(currentXStackFrame.getSourcePosition().getLine())) {
            return XStackBreakpointType.UNAVAILABLE;
        }
        return xStackBreakpointType;
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
            case DISABLE:
                this.myIcon = AllIcons.Debugger.Db_disabled_breakpoint;
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
