package com.runtime.pivot.plugin.listeners;

import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebugSessionListener;
import com.intellij.xdebugger.XSourcePosition;
import com.intellij.xdebugger.frame.XStackFrame;
import com.runtime.pivot.plugin.enums.BreakpointType;

public abstract class XStackFrameListener implements XDebugSessionListener {

    private final XDebugSession xDebugSession;
    private final XStackFrame xStackFrame;
    private final XSourcePosition xSourcePosition;
    private final Runnable compensateRunnable;

    protected XStackFrameListener(XDebugSession xDebugSession, XStackFrame xStackFrame, XSourcePosition xSourcePosition, Runnable compensateRunnable) {
        this.xDebugSession = xDebugSession;
        this.xStackFrame = xStackFrame;
        this.xSourcePosition = xSourcePosition;
        this.compensateRunnable = compensateRunnable;
    }

    public abstract void stackFrameExecutionMethod() throws Exception;

    @Override
    public void sessionPaused() {
        try {
            XStackFrame currentStackFrame = xDebugSession.getCurrentStackFrame();
            if (currentStackFrame.equals(xStackFrame)) {
                stackFrameExecutionMethod();
            }
            //TODO 判断是否到达位置,且不越界
            if (isAriveAtThePausePosition(xDebugSession.getCurrentStackFrame().getSourcePosition(),xSourcePosition)){
                xDebugSession.removeSessionListener(this);
            }else {
                if (compensateRunnable != null) {
                    compensateRunnable.run();
                }else {
                    xDebugSession.removeSessionListener(this);
                }
            }
        }catch (Exception exception){
            xDebugSession.removeSessionListener(this);
            throw new RuntimeException(exception);
        }
    }

    private boolean isAriveAtThePausePosition(XSourcePosition currentSourcePosition, XSourcePosition xSourcePosition) {
        if (xSourcePosition == null) {
            return true;
        }
        if (currentSourcePosition.getFile().getUrl().equals(xSourcePosition.getFile().getUrl())
                && currentSourcePosition.getLine()==(xSourcePosition).getLine()) {
            return true;
        }
        return false;
    }
}
